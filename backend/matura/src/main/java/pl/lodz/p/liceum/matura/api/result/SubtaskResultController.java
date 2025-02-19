package pl.lodz.p.liceum.matura.api.result;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResultService;
import pl.lodz.p.liceum.matura.domain.result.TestResult;
import pl.lodz.p.liceum.matura.domain.result.TestResultService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/subtaskResults")
public class SubtaskResultController {

    private final SubtaskResultService subtaskResultService;
    private final TestResultService testResultService;
    private final SubtaskResultDtoMapper mapper;
    private final TestResultDtoMapper testRestltMapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<SubtaskResultDto> getSubtaskResult(@PathVariable Integer id) {
        SubtaskResult subtaskResult = subtaskResultService.findById(id);
        return ResponseEntity.ok(mapper.toDto(subtaskResult));
    }

    @GetMapping(path = "/{id}/testResults")
    public ResponseEntity<List<TestResultDto>> getResults(@PathVariable Integer id) {
        List<TestResult> testResults = testResultService.findBySubtaskResult(id);
        return ResponseEntity.ok(
                testResults.stream()
                        .map(testRestltMapper::toDto)
                        .toList());
    }
}
