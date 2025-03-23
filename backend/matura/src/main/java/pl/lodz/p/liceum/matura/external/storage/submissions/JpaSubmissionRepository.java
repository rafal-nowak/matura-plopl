package pl.lodz.p.liceum.matura.external.storage.submissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSubmissionRepository extends JpaRepository<SubmissionEntity, Integer> {
    List<SubmissionEntity> findByTaskIdOrderBySubmittedAt(Integer taskId);
}
