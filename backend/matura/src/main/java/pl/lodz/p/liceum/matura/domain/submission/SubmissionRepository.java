package pl.lodz.p.liceum.matura.domain.submission;

import pl.lodz.p.liceum.matura.domain.subtask.Subtask;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository {
    Submission save(Submission submission);

    void update(Submission submission);

    void remove(Integer id);

    Optional<Submission> findById(Integer id);

    List<Submission> findByTaskId(Integer taskId);
}
