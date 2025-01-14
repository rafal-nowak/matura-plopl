package pl.lodz.p.liceum.matura.external.storage.result;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSubtaskResultRepository extends JpaRepository<SubtaskResultEntity, Integer> {
    List<SubtaskResultEntity> findBySubmissionId(Integer submissionId);
}
