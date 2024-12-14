package pl.lodz.p.liceum.matura.appservices;

public record UserRegistrationRequest(
        String email,
        String username,
        String password
) {
}
