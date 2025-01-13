package pl.lodz.p.liceum.matura.external.worker.task;

import pl.lodz.p.liceum.matura.external.worker.task.definition.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DockerComposeGenerator {
    public void generate(String path, Subtask subtask, TaskLimits limits) {
        File file = new File(path);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(getContent(subtask, limits));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getContent(Subtask subtask, TaskLimits limits) {
        // TODO get source file name
        String template = """
                services:
                  env:
                    image: python:3.8-alpine
                    command: [ "sh", "-c", "
                        cd code;
                        python3 -c "import task; task.%s()"
                      " ]
                    volumes:
                      - .:/code
                    mem_limit: %s  # 6m - Ograniczenie pamięci do 6 MB
                    ulimits:
                      cpu: %s         # 5 - Maksymalnie 5 sekundy czasu CPU
                """;
        return String.format(template, subtask.getTestedFunctionName(), limits.getMemory(), limits.getTime());
    }
}
