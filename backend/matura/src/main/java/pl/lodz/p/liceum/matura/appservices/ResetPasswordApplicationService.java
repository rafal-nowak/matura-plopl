package pl.lodz.p.liceum.matura.appservices;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.domain.resetpassword.PasswordNotChangedException;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordService;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordToken;
import pl.lodz.p.liceum.matura.domain.user.User;
import pl.lodz.p.liceum.matura.domain.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Log
public class ResetPasswordApplicationService {

    private final ResetPasswordService resetPasswordService;
    private final UserService userService;
    private final IEmail emailService;

    public void generateResetPasswordToken(String email) {
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(email);
        log.info("Reset password token created: " + resetPasswordToken);
        // send reset password token via email
        emailService.send(
                new Email(
                        "Reset Password Token",
                        Collections.singletonList(email),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        "Your reset token: " + resetPasswordToken.getToken()
                )
        );

    }

    @Transactional
    public void resetPassword(String email,
                                String resetPasswordToken,
                                String newPassword) {

        if (resetPasswordService.doesThisTokenAllowTheUserToChangeTheirPassword(resetPasswordToken, email)){
            final User user = userService.findByEmail(email);
            userService.update(user.withPassword(newPassword));
            resetPasswordService.deleteResetPasswordToken(resetPasswordToken);
        } else {
            throw new PasswordNotChangedException();
        }


    }
}
