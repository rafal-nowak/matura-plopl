package pl.lodz.p.liceum.matura.api.resetpassword;

public record ResetPasswordRequest(
        String email,
        String passwordResetToken,
        String newPassword
) {
}
