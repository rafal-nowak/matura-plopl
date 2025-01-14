package pl.lodz.p.liceum.matura.domain.result;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TestResultService {
    private final TestResultRepository testResultRepository;

    public TestResult save(TestResult testResult) {
        return testResultRepository.save(testResult);
    }

    public void update(TestResult testResult) {
        testResultRepository.update(testResult);
    }

    public void removeById(Integer id) {
        testResultRepository.remove(id);
    }

    public TestResult findById(Integer id) {
        return testResultRepository
                .findById(id)
                .orElseThrow(ResultNotFoundException::new);
    }
    public List<TestResult> findBySubtaskResult(Integer submissionId) {
        return testResultRepository.findBySubtaskResultId(submissionId);
    }
}
