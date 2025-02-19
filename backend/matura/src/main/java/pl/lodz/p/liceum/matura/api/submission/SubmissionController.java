package pl.lodz.p.liceum.matura.api.submission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.liceum.matura.api.result.SubtaskResultDto;
import pl.lodz.p.liceum.matura.api.result.SubtaskResultDtoMapper;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResultService;
import pl.lodz.p.liceum.matura.domain.submission.Submission;
import pl.lodz.p.liceum.matura.domain.submission.SubmissionService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionDtoMapper submissionMapper;
    private final SubtaskResultService subtaskResultService;
    private final SubtaskResultDtoMapper subtaskResultMapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<SubmissionDto> getSubmission(@PathVariable Integer id) {
        Submission submission = submissionService.findById(id);
        return ResponseEntity.ok(submissionMapper.toDto(submission));
    }

    @GetMapping(path = "/{id}/subtaskResults")
    public ResponseEntity<List<SubtaskResultDto>> getResults(@PathVariable Integer id) {
        List<SubtaskResult> subtaskResults = subtaskResultService.findBySubmissionId(id);
        return ResponseEntity.ok(
                subtaskResults.stream()
                        .map(subtaskResultMapper::toDto)
                        .toList());
    }
}
