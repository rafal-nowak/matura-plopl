package pl.lodz.p.liceum.matura.domain.result;

import java.util.List;
import java.util.Optional;

public interface SubtaskResultRepository {
    SubtaskResult save(SubtaskResult subtaskResult);

    void update(SubtaskResult subtaskResult);

    void remove(Integer id);


    Optional<SubtaskResult> findById(Integer id);
    List<SubtaskResult> findBySubmissionId(final Integer submissionId);
}
