package pl.lodz.p.liceum.matura.api.resetpassword;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.liceum.matura.appservices.ResetPasswordApplicationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class ResetPasswordTokenController {

    private final ResetPasswordApplicationService resetPasswordApplicationService;

    @PostMapping("reset/token")
    public ResponseEntity<Void> generatePasswordResetToken(@RequestBody GeneratePasswordResetTokenRequest request) {
        resetPasswordApplicationService.generateResetPasswordToken(request.email());

        return ResponseEntity.ok().build();
    }

    @PostMapping("reset/password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        resetPasswordApplicationService.resetPassword(
                request.email(),
                request.passwordResetToken(),
                request.newPassword());

        return ResponseEntity.ok().build();
    }

}
