package pl.lodz.p.liceum.matura.domain.result;

import java.util.List;
import java.util.Optional;

public interface TestResultRepository {
    TestResult save(TestResult testResult);

    void update(TestResult testResult);

    void remove(Integer id);


    Optional<TestResult> findById(Integer id);
    List<TestResult> findBySubtaskResultId(final Integer submissionId);
}
