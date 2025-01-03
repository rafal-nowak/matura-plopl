package pl.lodz.p.liceum.matura.domain;

import lombok.Data;

@Data
public class TestResult {
    private Verdict verdict;
    private Integer time;
    private Integer memory;
    private String message;
}
