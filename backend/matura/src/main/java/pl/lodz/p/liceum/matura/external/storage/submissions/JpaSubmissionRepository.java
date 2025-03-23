package pl.lodz.p.liceum.matura.external.storage.submissions;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.liceum.matura.domain.submission.Submission;

import java.util.List;

public interface JpaSubmissionRepository extends JpaRepository<SubmissionEntity, Integer> {
    List<SubmissionEntity> findByTaskIdOrderBySubmittedAt(Integer taskId);
}
