package pl.lodz.p.liceum.matura.external.storage.result;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.dao.DataIntegrityViolationException;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;
import pl.lodz.p.liceum.matura.domain.result.ResultAlreadyExistsException;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResultRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log
public class SubtaskResultStorageAdapter implements SubtaskResultRepository {

    private final JpaSubtaskResultRepository repository;
    private final SubtaskResultEntityMapper mapper;

    @Override
    public SubtaskResult save(final SubtaskResult subtaskResult) {
        try {
            SubtaskResultEntity saved = repository.save(mapper.toEntity(subtaskResult));
            log.info("Saved entity " + saved);
            return mapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            log.warning("SubtaskResult " + subtaskResult.getId() + " already exists");
            throw new ResultAlreadyExistsException();
        }
    }

    @Override
    public void update(final SubtaskResult subtaskResult) {
        repository.findById(subtaskResult.getId()).ifPresent(taskEntity -> repository.save(mapper.toEntity(subtaskResult)));
    }

    @Override
    public void remove(final Integer id) {
        repository.findById(id).ifPresent(resultEntity -> repository.deleteById(id));
    }

    @Override
    public Optional<SubtaskResult> findById(final Integer id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SubtaskResult> findBySubmissionId(final Integer submissionId) {
        return repository.findBySubmissionId(submissionId).stream().map(mapper::toDomain).toList();
    }
}
