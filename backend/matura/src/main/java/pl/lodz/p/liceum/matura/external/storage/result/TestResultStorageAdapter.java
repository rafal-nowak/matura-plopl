package pl.lodz.p.liceum.matura.external.storage.result;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.dao.DataIntegrityViolationException;
import pl.lodz.p.liceum.matura.domain.result.ResultAlreadyExistsException;
import pl.lodz.p.liceum.matura.domain.result.TestResult;
import pl.lodz.p.liceum.matura.domain.result.TestResultRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log
public class TestResultStorageAdapter implements TestResultRepository {

    private final JpaTestResultRepository repository;
    private final TestResultEntityMapper mapper;

    @Override
    public TestResult save(final TestResult testResult) {
        try {
            TestResultEntity saved = repository.save(mapper.toEntity(testResult));
            log.info("Saved entity " + saved);
            return mapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            log.warning("TestResult " + testResult.getId() + " already exists");
            throw new ResultAlreadyExistsException();
        }
    }

    @Override
    public void update(final TestResult testResult) {
        repository.findById(testResult.getId()).ifPresent(taskEntity -> repository.save(mapper.toEntity(testResult)));
    }

    @Override
    public void remove(final Integer id) {
        repository.findById(id).ifPresent(resultEntity -> repository.deleteById(id));
    }

    @Override
    public Optional<TestResult> findById(final Integer id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TestResult> findBySubtaskResultId(final Integer subtaskResultId) {
        return repository.findBySubtaskResultId(subtaskResultId).stream().map(mapper::toDomain).toList();
    }
}
