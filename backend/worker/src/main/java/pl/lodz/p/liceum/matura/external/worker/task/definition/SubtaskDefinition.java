package pl.lodz.p.liceum.matura.external.worker.task.definition;

import lombok.Data;

import java.util.Map;

@Data
public class SubtaskDefinition {

    Map<String, CheckData> checkTypes;
    String testedFunctionName;
    String userInputFilename;
    String userOutputFilename;
}
