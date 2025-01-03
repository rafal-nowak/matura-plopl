package pl.lodz.p.liceum.matura.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subtask extends Task {
    Integer number;
    TestType type;

    public Subtask(String workspaceUrl, Integer number, TestType type) {
        this(number, type);
        this.workspaceUrl = workspaceUrl;
    }
}
