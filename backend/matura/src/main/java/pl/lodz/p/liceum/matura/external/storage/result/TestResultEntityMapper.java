package pl.lodz.p.liceum.matura.external.storage.result;

import org.mapstruct.Mapper;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;
import pl.lodz.p.liceum.matura.domain.result.TestResult;

@Mapper(componentModel = "spring")
public interface TestResultEntityMapper {
    TestResultEntity toEntity(TestResult domain);
    TestResult toDomain(TestResultEntity entity);
}
