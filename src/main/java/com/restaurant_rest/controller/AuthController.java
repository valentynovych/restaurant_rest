package com.restaurant_rest.controller;

import com.restaurant_rest.entity.RefreshToken;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.exception.RefreshTokenException;
import com.restaurant_rest.model.EmailConfirm;
import com.restaurant_rest.model.authetnticate.AuthRequest;
import com.restaurant_rest.model.authetnticate.EmailConfirmRequest;
import com.restaurant_rest.model.authetnticate.JwtResponse;
import com.restaurant_rest.model.authetnticate.TokenRefreshRequest;
import com.restaurant_rest.service.MailService;
import com.restaurant_rest.service.RefreshTokenService;
import com.restaurant_rest.service.UserDetailsServiceImpl;
import com.restaurant_rest.service.UserService;
import com.restaurant_rest.utils.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Operation(
            description = "Send your email on body request",
            summary = "Login",
            responses = {
                    @ApiResponse(responseCode = "200",description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "User Not Found")
            })
    @PostMapping("signin")
    public ResponseEntity<?> login(@RequestBody AuthRequest email) {
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

        } catch (NullPointerException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    @Operation(
            description = "Send your email and confirm code on request body",
            summary = "Confirming email code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "User Not Found")
            })
    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmailCode(@RequestBody EmailConfirm confirm) {
        String accessToken = null;
        RefreshToken refreshToken = null;

        User userByEmail = userService.getUserByEmail(confirm.getEmail());
        if (userByEmail != null && confirm.getConfirmCode() != null) {
            String confirmEmail = userByEmail.getConfirmEmail();
            if (confirmEmail == null) {
                return new ResponseEntity<>("You cant confirm email before signin request", HttpStatus.BAD_REQUEST);
            } else if (!confirmEmail.equals(confirm.getConfirmCode())) {
                return new ResponseEntity<>("Confirm code is wrong", HttpStatus.CONFLICT);
            }

            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        confirm.getEmail(), null, AuthorityUtils.createAuthorityList("ROLE_USER"));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (BadCredentialsException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.successLogin(userByEmail);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userByEmail.getEmail());
            accessToken = tokenUtils.createAccessToken(userDetails);
            refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        }
        return new ResponseEntity<>(JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build(), HttpStatus.OK);
    }

    @Operation(
            description = "Send your refresh token for generate new token pair",
            summary = "Refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "403", description = "Refresh token is wrong!")
            })
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

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
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken,
                        "Refresh token is wrong!"));
    }
}
