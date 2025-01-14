package pl.lodz.p.liceum.matura.api.result;

import pl.lodz.p.liceum.matura.domain.result.Verdict;

import java.time.ZonedDateTime;

public record TestResultDto(
        Integer id,
        Integer subtaskResultId,
        Verdict verdict,
        Integer time,
        Integer memory,
        String message,
        ZonedDateTime createdAt
) {
}
