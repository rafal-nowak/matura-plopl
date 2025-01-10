package pl.lodz.p.liceum.matura.api.resetpassword;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.lodz.p.liceum.matura.BaseIT;
import pl.lodz.p.liceum.matura.TestUserFactory;
import pl.lodz.p.liceum.matura.api.auth.AuthenticationRequest;
import pl.lodz.p.liceum.matura.api.response.ErrorResponse;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordService;
import pl.lodz.p.liceum.matura.domain.resetpassword.ResetPasswordToken;
import pl.lodz.p.liceum.matura.domain.user.User;
import pl.lodz.p.liceum.matura.domain.user.UserService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log
class ResetPasswordTokenControllerIT extends BaseIT {

    @Autowired
    UserService service;

    @Autowired
    ResetPasswordService resetPasswordService;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2025-01-01T12:00:00Z"), ZoneId.of("UTC"));

    @Test
    void user_should_be_able_to_generate_reset_access_token() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        GeneratePasswordResetTokenRequest request = new GeneratePasswordResetTokenRequest(user.getEmail());

        //when
        var response = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/reset/token",
                null,
                request,
                ResponseEntity.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void user_should_be_able_to_change_forgotten_password_using_valid_reset_access_token() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(user.getEmail());
        ResetPasswordRequest request = new ResetPasswordRequest(
                user.getEmail(),
                resetPasswordToken.getToken(),
                "newPassword"
        );

        //when
        var response = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/reset/password",
                null,
                request,
                ResponseEntity.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void user_should_be_able_to_change_forgotten_password_using_valid_reset_access_token_only_once() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(user.getEmail());
        ResetPasswordRequest request = new ResetPasswordRequest(
                user.getEmail(),
                resetPasswordToken.getToken(),
                "newPassword"
        );

        //when
        var response = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/reset/password",
                null,
                request,
                ResponseEntity.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //when
        var response1 = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/reset/password",
                null,
                request,
                ErrorResponse.class);

        //then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response1.getStatusCode());
    }

    @Test
    void user_should_not_be_able_to_change_forgotten_password_using_invalid_reset_access_token() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(user.getEmail());
        ResetPasswordRequest request = new ResetPasswordRequest(
                user.getEmail(),
                resetPasswordToken.getToken() + "abc",
                "newPassword"
        );

        //when
        var response = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/reset/password",
                null,
                request,
                ErrorResponse.class);

        //then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void user_should_get_response_code_success_when_logging_in_with_new_password() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(user.getEmail());
        ResetPasswordRequest request = new ResetPasswordRequest(
                user.getEmail(),
                resetPasswordToken.getToken(),
                "newPassword"
        );

        //when
        var response = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/reset/password",
                null,
                request,
                ResponseEntity.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(user.getEmail(),"newPassword");

        //when
        var response1 = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/login",
                null,
                authenticationRequest,
                ErrorResponse.class);

        //then
        assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @Test
    void user_should_be_able_to_change_forgotten_password_using_valid_reset_access_token_() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(user.getEmail());
        final Boolean userCanChangePassword = resetPasswordService.doesThisTokenAllowTheUserToChangeTheirPassword(
                resetPasswordToken.getToken(),
                user.getEmail());

        //then
        assertTrue(userCanChangePassword);
    }

    @Test
    void user_should_not_be_able_to_change_forgotten_password_using_invalid_reset_access_token_() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        User user1 = TestUserFactory.createStudent();
        service.save(user1);
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(user1.getEmail());
        final Boolean userCanChangePassword = resetPasswordService.doesThisTokenAllowTheUserToChangeTheirPassword(
                resetPasswordToken.getToken(),
                user.getEmail());

        //then
        assertFalse(userCanChangePassword);
    }

    @Test
    void user_should_not_be_able_to_change_forgotten_password_using_obsolete_reset_access_token() {

        //given
        User user = TestUserFactory.createStudent();
        service.save(user);
        final ResetPasswordToken resetPasswordToken = resetPasswordService.createResetPasswordToken(user.getEmail(), fixedClock);
        ResetPasswordRequest request = new ResetPasswordRequest(
                user.getEmail(),
                resetPasswordToken.getToken(),
                "newPassword"
        );

        //when
        var response = callHttpMethod(HttpMethod.POST,
                "/api/v1/auth/reset/password",
                null,
                request,
                ErrorResponse.class);

        //then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

}