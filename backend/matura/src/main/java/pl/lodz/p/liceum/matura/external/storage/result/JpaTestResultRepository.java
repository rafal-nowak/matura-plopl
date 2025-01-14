package pl.lodz.p.liceum.matura.external.storage.result;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaTestResultRepository extends JpaRepository<TestResultEntity, Integer> {
    List<TestResultEntity> findBySubtaskResultId(Integer subtaskResultId);
}
