package pl.lodz.p.liceum.matura.api.result;

import org.mapstruct.Mapper;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;

@Mapper(componentModel = "spring")
public interface SubtaskResultDtoMapper {
    SubtaskResultDto toDto(SubtaskResult domain);
    SubtaskResult toDomain(SubtaskResultDto dto);
}
