package pl.lodz.p.liceum.matura.external.storage.resetpassword;

import org.mapstruct.Mapper;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordToken;


@Mapper(componentModel = "spring")
public interface ResetPasswordTokenEntityMapper {

    ResetPasswordTokenEntity toEntity(ResetPasswordToken domain);

    ResetPasswordToken toDomain(ResetPasswordTokenEntity entity);

}
