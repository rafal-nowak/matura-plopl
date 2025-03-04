package pl.lodz.p.liceum.matura.external.worker.task;

import pl.lodz.p.liceum.matura.external.worker.task.definition.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DockerComposeGenerator {
    public void generate(String path, SubtaskDefinition subtaskDefinition, TaskLimits limits) {
        File file = new File(path);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(getContent(subtaskDefinition, limits));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getContent(SubtaskDefinition subtaskDefinition, TaskLimits limits) {
        // TODO get source file name
        String template = """
                services:
                  env:
                    image: python:3.8-bookworm
                    command: [ "sh", "-c", '
                        cd code/src;
                        chmod +x ../sio2jail;
                        ../sio2jail -f 3 -o oiuser --perf off --mount-namespace off --pid-namespace off --uts-namespace off --ipc-namespace off --net-namespace off --capability-drop off --user-namespace off -s -m %dK --utimelimit %dms -- /usr/bin/python3 -c "import task; task.%s()" 3> ../sio2jail_output.txt > ../user_standard_output.txt
                      ' ]
                    volumes:
                      - .:/code
                    mem_limit: %s  # 6m - Ograniczenie pamięci do 6 MB
                    ulimits:
                      cpu: %s         # 5 - Maksymalnie 5 sekundy czasu CPU
                """;
        return String.format(template, limits.getMemory(), limits.getTime() * 2, subtaskDefinition.getTestedFunctionName(), limits.getMemory() * 2 + 50000 + "kb", (int) Math.ceil(limits.getTime() * 2 / 1000d) + 5);
    }
}