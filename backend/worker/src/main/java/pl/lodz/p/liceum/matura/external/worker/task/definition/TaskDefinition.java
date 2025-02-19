package pl.lodz.p.liceum.matura.external.worker.task.definition;

import lombok.Data;

import java.util.Map;

@Data
public class TaskDefinition {
    String sourceFile;
    TaskLimits limits;
    Map<String, SubtaskDefinition> tasks;
}
