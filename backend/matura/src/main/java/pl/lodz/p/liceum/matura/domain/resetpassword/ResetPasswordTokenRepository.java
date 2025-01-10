package pl.lodz.p.liceum.matura.domain.resetpassword;

import java.util.Optional;

public interface ResetPasswordTokenRepository {
    void save(ResetPasswordToken token);
    Optional<ResetPasswordToken> findByToken(String token);
    void deleteToken(String token);
}
