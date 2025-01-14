package pl.lodz.p.liceum.matura.external.storage.result;

import org.mapstruct.Mapper;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;

@Mapper(componentModel = "spring")
public interface SubtaskResultEntityMapper {
    SubtaskResultEntity toEntity(SubtaskResult domain);
    SubtaskResult toDomain(SubtaskResultEntity entity);
}
