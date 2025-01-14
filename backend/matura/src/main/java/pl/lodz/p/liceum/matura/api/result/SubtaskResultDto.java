package pl.lodz.p.liceum.matura.api.result;

import java.time.ZonedDateTime;

public record SubtaskResultDto(
        Integer id,
        Integer submissionId,
        Integer subtaskNumber,
        String description,
        Integer score,
        ZonedDateTime createdAt
) {
}
