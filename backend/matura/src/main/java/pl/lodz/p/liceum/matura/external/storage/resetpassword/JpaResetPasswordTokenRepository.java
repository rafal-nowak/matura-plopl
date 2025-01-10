package pl.lodz.p.liceum.matura.external.storage.resetpassword;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaResetPasswordTokenRepository extends JpaRepository<ResetPasswordTokenEntity, Integer> {
    Optional<ResetPasswordTokenEntity> findByToken(String token);
    void deleteByToken(String token);
}
