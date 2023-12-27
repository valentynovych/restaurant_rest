package com.restaurant_rest.controller;

import com.restaurant_rest.entity.RefreshToken;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.model.SimpleError;
import com.restaurant_rest.model.authetnticate.*;
import com.restaurant_rest.service.MailService;
import com.restaurant_rest.service.RefreshTokenService;
import com.restaurant_rest.service.UserDetailsServiceImpl;
import com.restaurant_rest.service.UserService;
import com.restaurant_rest.utils.JwtTokenUtils;
import io.jsonwebtoken.SignatureException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final MailService mailService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = EmailConfirmRequest.class))}),
    })
    @PostMapping("signin")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest email) {
        try {
            User userByEmail = userService.getUserByEmail(email.getEmail());
            String confirmCode = mailService.sendEmailConfirmCode(userByEmail.getEmail()).get();
            userService.saveUserConfirmCode(userByEmail, confirmCode);
            return new ResponseEntity<>(
                    EmailConfirmRequest.builder()
                            .email(userByEmail.getEmail())
                            .confirmCode(confirmCode)
                            .message("Please confirm your email! \n Send this confirmCode to ../confirmEmail")
                            .build(),
                    HttpStatus.OK);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    @Operation(summary = "Confirm email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = EmailConfirmRequest.class))}),
    })
    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmailCode(@Valid @RequestBody EmailConfirm confirm) {

        User userByEmail = userService.getUserByEmail(confirm.getEmail());
        String confirmEmail = userByEmail.getConfirmEmail();
        if (confirmEmail == null) {
            return new ResponseEntity<>("You cant confirm email before signin request", HttpStatus.BAD_REQUEST);
        } else if (!confirmEmail.equals(confirm.getConfirmCode())) {
            return new ResponseEntity<>("Confirm code is wrong", HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    confirm.getEmail(), null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        JwtResponse jwtResponse = userService.successLogin(userByEmail);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @Operation(summary = "Refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = EmailConfirmRequest.class))}),
    })
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        tokenUtils.validateJwtToken(requestRefreshToken);
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = tokenUtils.createAccessToken(
                            userDetailsService.loadUserByUsername(user.getUsername()));
                    return new ResponseEntity<>(JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestRefreshToken)
                            .build(), HttpStatus.OK);
                })
                .orElseThrow(() -> new SignatureException("Invalid refresh token"));
    }

    @Operation(summary = "Register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = EmailConfirmRequest.class))}),
    })
    @PostMapping("signup")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest email) {
        try {
            String email1 = email.getEmail();
            String confirmCode = mailService.sendEmailConfirmCode(email1).get();
            userService.registerNewUser(email1, confirmCode);
            return new ResponseEntity<>(
                    EmailConfirmRequest.builder()
                            .email(email1)
                            .confirmCode(confirmCode)
                            .message("Please confirm your email! \n Send this confirmCode to ../confirmEmail")
                            .build(),
                    HttpStatus.OK);

        }catch (EntityExistsException e) {
            return new ResponseEntity<>(new SimpleError(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }
}
