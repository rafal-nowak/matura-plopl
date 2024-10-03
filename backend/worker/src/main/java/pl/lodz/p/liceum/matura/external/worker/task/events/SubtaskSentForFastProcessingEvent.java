package pl.lodz.p.liceum.matura.external.worker.task.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubtaskSentForFastProcessingEvent extends TaskEvent {
    Integer taskId;
    Integer submissionId;
    String workspaceUrl;
    Integer number;
}
