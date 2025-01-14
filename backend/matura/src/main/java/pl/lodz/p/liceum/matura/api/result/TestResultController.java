package pl.lodz.p.liceum.matura.api.result;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;
import pl.lodz.p.liceum.matura.domain.result.TestResult;
import pl.lodz.p.liceum.matura.domain.result.TestResultService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/testResults")
public class TestResultController {

    private final TestResultService testResultService;
    private final TestResultDtoMapper mapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<TestResultDto> getTestResult(@PathVariable Integer id) {
        TestResult testResult = testResultService.findById(id);
        return ResponseEntity.ok(mapper.toDto(testResult));
    }
}
