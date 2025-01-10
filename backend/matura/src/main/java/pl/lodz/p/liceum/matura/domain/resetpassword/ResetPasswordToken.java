package pl.lodz.p.liceum.matura.domain.resetpassword;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class ResetPasswordToken {
    Integer id;
    String email;
    String token;
    ZonedDateTime expiresAt;

    public static ResetPasswordToken generateFor(String email) {
        return new ResetPasswordToken(null, email, UUID.randomUUID().toString(), ZonedDateTime.now().plusSeconds(60));
    }

    public boolean isValidForUser(String username) {
        if (!username.equals(email)) {
            return false;
        }

        return !ZonedDateTime.now().isAfter(expiresAt);
    }
}
