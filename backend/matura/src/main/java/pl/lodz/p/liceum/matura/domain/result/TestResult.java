package pl.lodz.p.liceum.matura.domain.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResult {
    private Integer id;
    private Integer subtaskResultId;
    private Verdict verdict;
    private Integer time;
    private Integer memory;
    private String message;
    private ZonedDateTime createdAt;

}
