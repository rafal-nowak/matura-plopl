package pl.lodz.p.liceum.matura.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    Integer taskId;
    String workspaceUrl;
}
