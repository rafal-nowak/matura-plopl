package pl.lodz.p.liceum.matura.external.storage.resetpassword;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordToken;

import java.time.Clock;


@Mapper(componentModel = "spring")
public interface ResetPasswordTokenEntityMapper {

    ResetPasswordTokenEntityMapper INSTANCE = Mappers.getMapper(ResetPasswordTokenEntityMapper.class);

    @Mapping(target = "clock", ignore = true) // Clock nie jest przechowywany w encji
    ResetPasswordToken toDomain(ResetPasswordTokenEntity entity, Clock clock);

    ResetPasswordTokenEntity toEntity(ResetPasswordToken domain);

    default ResetPasswordToken mapToDomainWithClock(ResetPasswordTokenEntity entity, Clock clock) {
        ResetPasswordToken domain = toDomain(entity, clock);
        domain.setClock(clock); // Ustawienie zegara w obiekcie domenowym
        return domain;
    }

}
