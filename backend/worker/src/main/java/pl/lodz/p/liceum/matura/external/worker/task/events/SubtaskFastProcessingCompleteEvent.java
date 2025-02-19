package pl.lodz.p.liceum.matura.external.worker.task.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.liceum.matura.domain.TestResult;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubtaskFastProcessingCompleteEvent extends TaskEvent {
    Integer taskId;
    Integer submissionId;
    String workspaceUrl;
    Integer number;
    List<TestResult> testResults;
}
