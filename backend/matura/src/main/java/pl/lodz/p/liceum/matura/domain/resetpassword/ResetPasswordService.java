package pl.lodz.p.liceum.matura.domain.resetpassword;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ResetPasswordService {
    private final ResetPasswordTokenRepository repository;

    public ResetPasswordToken createResetPasswordToken(String email) {
        final ResetPasswordToken resetPasswordToken = ResetPasswordToken.generateFor(email);
        repository.save(resetPasswordToken);
        return resetPasswordToken;
    }

    public void deleteResetPasswordToken(String token) {
        repository.deleteToken(token);
    }

    public Boolean doesThisTokenAllowTheUserToChangeTheirPassword(String token, String email) {
        final Optional<ResetPasswordToken> maybeToken = repository.findByToken(token);
        return maybeToken.map(resetPasswordToken -> resetPasswordToken.isValidForUser(email)).orElse(false);
    }
}
