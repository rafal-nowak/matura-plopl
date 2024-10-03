package pl.lodz.p.liceum.matura.domain.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private Integer id;
    private Integer userId;
    private Integer templateId;
    private String workspaceUrl;
    private TaskState state;
    private Integer createdBy;
    private ZonedDateTime createdAt;

    public boolean isUserTheOwnerOfThisTask(Integer userId) {
        return createdBy.equals(userId);
    }
}
