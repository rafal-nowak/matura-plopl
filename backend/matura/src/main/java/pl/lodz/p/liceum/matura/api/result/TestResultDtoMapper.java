package pl.lodz.p.liceum.matura.api.result;

import org.mapstruct.Mapper;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;
import pl.lodz.p.liceum.matura.domain.result.TestResult;

@Mapper(componentModel = "spring")
public interface TestResultDtoMapper {
    TestResultDto toDto(TestResult domain);
    TestResult toDomain(TestResultDto dto);
}
