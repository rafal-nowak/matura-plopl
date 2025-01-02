package pl.lodz.p.liceum.matura.external.worker.task;

import pl.lodz.p.liceum.matura.external.worker.task.definition.CheckData;
import pl.lodz.p.liceum.matura.external.worker.task.definition.TaskDefinition;
import pl.lodz.p.liceum.matura.external.worker.task.definition.TaskEnvironment;
import pl.lodz.p.liceum.matura.external.worker.task.definition.TaskLimits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DockerComposeGenerator {
    public void generate(String path, TaskEnvironment taskEnvironment, TaskLimits limits, CheckData checkData) {
        File file = new File(path);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(getContent(taskEnvironment, checkData, limits));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getContent(TaskEnvironment taskEnvironment, CheckData checkData, TaskLimits limits) {
        String template = """
                version: '3'
                                
                services:
                  env:
                    image: %s
                    command: [ "sh", "-c", "
                        cd code;
                        %s
                      " ]
                    volumes:
                      - .:/code
                    mem_limit: %s  # 6m - Ograniczenie pamięci do 6 MB
                    ulimits:
                      cpu: %s         # 5 - Maksymalnie 5 sekundy czasu CPU
                                
                """;
        return String.format(template, taskEnvironment.getImage(), checkData.getVerification(), limits.getMemory(), limits.getTime());
    }
}
