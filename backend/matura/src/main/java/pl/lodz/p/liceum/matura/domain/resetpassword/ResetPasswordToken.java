package pl.lodz.p.liceum.matura.domain.resetpassword;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.Clock;
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
    Clock clock;

    public static ResetPasswordToken generateFor(String email, Clock clock) {
        return new ResetPasswordToken(
                null,
                email,
                UUID.randomUUID().toString(),
                ZonedDateTime.now(clock).plusSeconds(60),
                clock);
    }

    public boolean isValidForUser(String username) {
        if (!username.equals(email)) {
            return false;
        }

        return !ZonedDateTime.now(clock).isAfter(expiresAt);
    }
}
