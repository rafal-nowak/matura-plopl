package pl.lodz.p.liceum.matura.external.storage.resetpassword;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.dao.DataIntegrityViolationException;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordToken;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordTokenNotGeneratedException;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordTokenRepository;

import java.time.Clock;
import java.util.Optional;

@RequiredArgsConstructor
@Log
public
class ResetPasswordTokenStorageAdapter implements ResetPasswordTokenRepository {

    private final JpaResetPasswordTokenRepository repository;
    private final ResetPasswordTokenEntityMapper mapper;
    private final Clock clock;


    @Override
    public void save(final ResetPasswordToken token) {
        try {
            ResetPasswordTokenEntity saved = repository.save(mapper.toEntity(token));
            log.info("Saved entity " + saved);
        } catch (DataIntegrityViolationException ex) {
            log.warning("Token " + token + " \n" +
                    "has not been saved to the database");
            throw new ResetPasswordTokenNotGeneratedException();
        }
    }

    @Override
    public Optional<ResetPasswordToken> findByToken(final String token) {
        Optional<ResetPasswordTokenEntity> optional = repository.findByToken(token);
        return optional.map(resetPasswordTokenEntity -> mapper.mapToDomainWithClock(resetPasswordTokenEntity, clock));

    }

    @Override
    public void deleteToken(final String token) {
        repository.deleteByToken(token);
    }
}
