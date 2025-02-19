package pl.lodz.p.liceum.matura.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.domain.*;
import pl.lodz.p.liceum.matura.external.worker.task.DockerComposeGenerator;
import pl.lodz.p.liceum.matura.external.worker.task.TaskDefinitionParser;
import pl.lodz.p.liceum.matura.external.worker.task.definition.CheckData;
import pl.lodz.p.liceum.matura.external.worker.task.definition.SubtaskDefinition;
import pl.lodz.p.liceum.matura.external.worker.task.definition.TaskDefinition;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Runtime.getRuntime;

@RequiredArgsConstructor
@Log
@Service
public class DockerTaskExecutor implements TaskExecutor {

    private final TaskDefinitionParser taskDefinitionParser;
    private final DockerComposeGenerator dockerComposeGenerator;

    private boolean execute(Task task, TestResult testResult) {
        Process process = null;
        try {
            StringBuilder outputLogs = new StringBuilder();
            StringBuilder errorLogs = new StringBuilder();

            var command = prepareCommand(task.getWorkspaceUrl());
            process = getRuntime().exec(command);
            log.info("Execution started");

            // Create separate threads to handle output and error streams
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), outputLogs::append);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorLogs::append);

            outputGobbler.start();
            errorGobbler.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            outputGobbler.join();
            errorGobbler.join();

            log.info("Execution finished");
            String logs = "Standard Output:\n" + outputLogs.toString() +
                    "\n\nStandard Error:\n" + errorLogs.toString();
            log.info(logs);

            if (exitCode != 0) {
                testResult.setVerdict(Verdict.SYSTEM_ERROR);
                log.info("Process exited with code " + exitCode);
                return false;
            }

            Pattern pattern = Pattern.compile("Container (\\S+)");
            Matcher matcher = pattern.matcher(logs);
            if (!matcher.find()) {
                log.warning("Could not extract container name from logs.");
                testResult.setVerdict(Verdict.SYSTEM_ERROR);
                return false;
            }
            String containerName = matcher.group(1);
            System.out.println("Container name: " + containerName);

            var inspectCommad = "docker inspect " + containerName;
            Process inspectProcess = getRuntime().exec(inspectCommad);
            StringBuilder inspectResult = new StringBuilder();
            StreamGobbler inspectGobbler = new StreamGobbler(inspectProcess.getInputStream(), inspectResult::append);
            inspectGobbler.start();
            inspectGobbler.join();
            var inspectLogs = "\ndocker inspect " + containerName + "\n" + inspectResult.toString();
            log.info(inspectLogs);

            String logFilePath = Paths.get(task.getWorkspaceUrl(), "log.txt").toString();
            saveLogs(logFilePath, logs + inspectLogs);
            log.info("Logged to file: " + logFilePath);


            // Analyse JSON with docker inspect
            ObjectMapper mapper = new ObjectMapper();
            JsonNode inspectJson = mapper.readTree(inspectResult.toString());
            JsonNode state = inspectJson.get(0).get("State");

            boolean oomKilled = state.get("OOMKilled").asBoolean();
            int inspectExitCode = state.get("ExitCode").asInt();

            log.info("OOMKilled: " + oomKilled);
            log.info("ExitCode: " + inspectExitCode);

            if (oomKilled) {
                testResult.setVerdict(Verdict.MEMORY_LIMIT_EXCEEDED);
                return false;
            }
            if (inspectExitCode == 137) {
                testResult.setVerdict(Verdict.TIME_LIMIT_EXCEEDED);
                return false;
            }
            if (inspectExitCode != 0) {
                testResult.setVerdict(Verdict.RUNTIME_ERROR);
                testResult.setMessage("Process exited with code " + inspectExitCode);
                return false;
            }

            return true;
        } catch (InterruptedException | IOException exception) {
            log.info("Exception during execution: " + exception);
            testResult.setVerdict(Verdict.SYSTEM_ERROR);
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private void saveLogs(final String path, final String content) {
        File file = new File(path);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkAnswer(Path userOutputFile, Path expectedOutputFile, TestResult testResult) {
        try {
            if (!Files.exists(userOutputFile)) {
                testResult.setVerdict(Verdict.WRONG_ANSWER);
                testResult.setMessage("No output file found");
                return false;
            }

            var userOutput = Files.readAllLines(userOutputFile);
            var expectedOutput = Files.readAllLines(expectedOutputFile);

            if (!userOutput.isEmpty() && userOutput.get(userOutput.size() - 1).isEmpty()) userOutput.remove(userOutput.size() - 1);
            if (!expectedOutput.isEmpty() && expectedOutput.get(expectedOutput.size() - 1).isEmpty()) expectedOutput.remove(expectedOutput.size() - 1);

            for (int i = 0; i < Math.max(userOutput.size(), expectedOutput.size()); i++) {
                if (i >= userOutput.size()) {
                    testResult.setVerdict(Verdict.WRONG_ANSWER);
                    testResult.setMessage("Expected: " + expectedOutput.get(i) + " found: EOF");
                    return false;
                }
                if (i >= expectedOutput.size()) {
                    testResult.setVerdict(Verdict.WRONG_ANSWER);
                    testResult.setMessage("User output contains more lines than expected");
                    return false;
                }
                var userLine = userOutput.get(i).trim();
                var expectedLine = userOutput.get(i).trim();
                if (!userLine.equals(expectedLine)) {
                    testResult.setVerdict(Verdict.WRONG_ANSWER);
                    testResult.setMessage("Expected: " + expectedOutput.get(i) + " found: " + userOutput.get(i));
                    return false;
                }
            }
        } catch (IOException e) {
            testResult.setVerdict(Verdict.SYSTEM_ERROR);
            log.info("Failed to read output files");
            return false;
        }
        return true;
    }

    private void prepareFile(Path inputFilePath, Path destinationPath) throws IOException {
        Files.copy(inputFilePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public List<TestResult> executeSubtask(Subtask subtask) {
        log.info("Subtask execution started");

        TaskDefinition taskDefinition = taskDefinitionParser.parse(subtask.getWorkspaceUrl() + "/task_definition.yml");

        // TODO make it more robust

        SubtaskDefinition subtaskDefinition = taskDefinition
                .getTasks()
                .get("task_" + subtask.getNumber());
        CheckData checkData = taskDefinition
                .getTasks()
                .get("task_" + subtask.getNumber())
                .getCheckTypes()
                .get(subtask.getType().toString());

        dockerComposeGenerator.generate(
                subtask.getWorkspaceUrl() + "/docker-compose.yml",
                taskDefinition.getTasks().get("task_" + subtask.getNumber()),
                taskDefinition.getLimits()
        );

        File inputDirectory = new File(subtask.getWorkspaceUrl() + '/' + checkData.getInputFilesDirectory());
        File[] inputFiles = inputDirectory.listFiles();

        if (inputFiles == null) {
            return List.of();
        }

        List<TestResult> results = new ArrayList<>();

        for (File inputFile : inputFiles) {
            Path outputFilePath = Paths.get(subtask.getWorkspaceUrl(), checkData.getOutputFilesDirectory(), inputFile.getName().replace(".in", ".out"));
            if (!Files.exists(outputFilePath)) {
                log.info("Skipping file: " + inputFile.getName());
                continue;
            }

            TestResult testResult = new TestResult();

            try {
                prepareFile(inputFile.toPath(), Paths.get(subtask.getWorkspaceUrl(), Path.of(taskDefinition.getSourceFile()).getParent().toString(), subtaskDefinition.getUserInputFilename()));
            } catch (IOException e) {
                testResult.setVerdict(Verdict.SYSTEM_ERROR);
                results.add(testResult);
                log.info("Failed to copy input file: " + inputFile.getName());
                continue;
            }
            if (execute(subtask, testResult)) {
                Path userOutputFile = Paths.get(subtask.getWorkspaceUrl(), Path.of(taskDefinition.getSourceFile()).getParent().toString(), subtaskDefinition.getUserOutputFilename());
                if (checkAnswer(userOutputFile, outputFilePath, testResult))
                    testResult.setVerdict(Verdict.ACCEPTED);
            }
            results.add(testResult);
        }
        return results;
    }

    private String[] prepareCommand(String workspaceUrl) {
        String dockerHost = "export DOCKER_HOST='tcp://localhost:2375'; ";
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows)
            return new String[]{"powershell.exe", "/c", "docker-compose --file \"" + workspaceUrl + "\\docker-compose.yml\" up"};
        else
            return new String[]{"sh", "-c", "cd " + workspaceUrl + "; docker compose up"};
//            return new String[]{"sh", "-c", dockerHost + "cd " + workspaceUrl + " && docker-compose up"};
    }
}
