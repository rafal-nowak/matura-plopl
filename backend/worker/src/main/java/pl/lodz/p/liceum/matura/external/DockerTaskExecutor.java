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
import pl.lodz.p.liceum.matura.external.worker.task.definition.TaskDefinition;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Runtime.getRuntime;

@RequiredArgsConstructor
@Log
@Service
public class DockerTaskExecutor implements TaskExecutor {

    private static final int SUCCESS = 0;
    private final TaskDefinitionParser taskDefinitionParser;
    private final DockerComposeGenerator dockerComposeGenerator;

    private ExecutionStatus execute(Task task) {
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

            Pattern pattern = Pattern.compile("Container (\\S+)");
            Matcher matcher = pattern.matcher(logs);
            if (!matcher.find()) {
                log.warning("Could not extract container name from logs.");
                return ExecutionStatus.FAILED;
            }
            String containerName = matcher.group(1);
            System.out.println("Nazwa kontenera: " + containerName);

            var inspectCommad = "docker inspect " + containerName;
            Process inspectProcess = getRuntime().exec(inspectCommad);
            StringBuilder inspectResult = new StringBuilder();
            StreamGobbler inspectGobbler = new StreamGobbler(inspectProcess.getInputStream(), inspectResult::append);
            inspectGobbler.start();
            inspectGobbler.join();
            var inspectLogs = "\ndocker inspect " + containerName + "\n" + inspectResult.toString();
            log.info(inspectLogs);

            String logFilePath = java.nio.file.Paths.get(task.getWorkspaceUrl(), "log.txt").toString();
            saveLogs(logFilePath, logs + inspectLogs);
            log.info("Logged to file: " + logFilePath);


            // Analiza JSON z docker inspect
            ObjectMapper mapper = new ObjectMapper();
            JsonNode inspectJson = mapper.readTree(inspectResult.toString());
            JsonNode state = inspectJson.get(0).get("State");

            boolean oomKilled = state.get("OOMKilled").asBoolean();
            int inspectExitCode = state.get("ExitCode").asInt();

            log.info("OOMKilled: " + oomKilled);
            log.info("ExitCode: " + inspectExitCode);

            return exitCode == 0 ? ExecutionStatus.COMPLETED : ExecutionStatus.FAILED;
        } catch (InterruptedException | IOException exception) {
            log.info("Exception during execution: " + exception);
            return ExecutionStatus.FAILED;
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

    private Result getSubtaskResult(Subtask subtask) {
        Result result = new Result();
        try {
            var description = Files.readString(Path.of(subtask.getWorkspaceUrl() + "/test_results/task_" + subtask.getNumber() + "/test_details.txt"));
            var summary = Files.readAllLines(Path.of(subtask.getWorkspaceUrl() + "/test_results/task_" + subtask.getNumber() + "/test_summary.txt"));
            result.setDescription(description);
            int score = Integer.parseInt(summary.get(1).split(" ")[4]) == 0 ? 0 : Integer.parseInt(summary.get(1).split(" ")[2]) * 100 / Integer.parseInt(summary.get(1).split(" ")[4]);
            result.setScore(score);
        } catch (IOException e) {
            throw new ResultFileNotFoundException();
        }
        return result;
    }

    @Override
    public Result executeTask(Task task) {
//        log.info("Task started");
//
//        TaskDefinition taskDefinition = taskDefinitionParser.parse(task.getWorkspaceUrl() + "/task_definition.yml");
//
//        dockerComposeGenerator.generate(
//                task.getWorkspaceUrl() + "/docker-compose.yml",
//                taskDefinition.getEnvironment(),
//                taskDefinition.getVerification()
//        );
//
//        return execute(task);
        throw new UnsupportedOperationException();
    }

    @Override
    public Result executeSubtask(Subtask subtask) {
        log.info("Subtask started");

        TaskDefinition taskDefinition = taskDefinitionParser.parse(subtask.getWorkspaceUrl() + "/task_definition.yml");

        CheckData checkData = taskDefinition
                .getTasks()
                .get("task_" + subtask.getNumber())
                .getCheckTypes()
                .get(subtask.getType().toString());

        dockerComposeGenerator.generate(
                subtask.getWorkspaceUrl() + "/docker-compose.yml",
                taskDefinition.getEnvironment(),
                taskDefinition.getLimits(),
                checkData
        );

        var executionStatus = execute(subtask);
        var result = getSubtaskResult(subtask);
        result.setExecutionStatus(executionStatus);
        return result;
    }

    private String[] prepareCommand(String workspaceUrl) {
        String dockerHost = "export DOCKER_HOST='tcp://localhost:2375'; ";
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows)
            return new String[]{"powershell.exe", "/c", "docker-compose --file \"" + workspaceUrl + "\\docker-compose.yml\" up"};
        else
            return new String[]{"sh", "-c", "cd " + workspaceUrl + "; docker-compose up"};
//            return new String[]{"sh", "-c", dockerHost + "cd " + workspaceUrl + " && docker-compose up"};
    }
}
