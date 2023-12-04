package com.restaurant_rest.controller;

import com.restaurant_rest.entity.User;
import com.restaurant_rest.model.EmailConfirm;
import com.restaurant_rest.model.authetnticate.AuthRequest;
import com.restaurant_rest.model.authetnticate.EmailConfirmRequest;
import com.restaurant_rest.model.authetnticate.JwtResponse;
import com.restaurant_rest.service.MailService;
import com.restaurant_rest.service.UserDetailsServiceImpl;
import com.restaurant_rest.service.UserService;
import com.restaurant_rest.utils.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Operation(
            description = "Send your email on body request",
            summary = "Login",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "User Not Found", responseCode = "404"),
                    @ApiResponse(description = "Bad Request", responseCode = "400")
            })
    @PostMapping("")
    public ResponseEntity<?> login(@RequestBody AuthRequest email) {
        try {
            User userByEmail = userService.getUserByEmail(email.getEmail());
            String confirmCode = mailService.sendEmailConfirmCode(userByEmail.getEmail()).get();
            userByEmail.setConfirmEmail(confirmCode);
            userService.saveUser(userByEmail);
            return new ResponseEntity<>(
                    EmailConfirmRequest
                            .builder()
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
            description = "Send your email on body request",
            summary = "Login",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "User Not Found", responseCode = "404"),
            })
    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmailCode(@RequestBody EmailConfirm confirm) {
        User userByEmail = userService.getUserByEmail(confirm.getEmail());
        String token = null;
        if (userByEmail != null && confirm.getConfirmCode() != null) {
            String confirmEmail = userByEmail.getConfirmEmail();
            if (!confirmEmail.equals(confirm.getConfirmCode())) {
                return new ResponseEntity<>("Confirm code is wrong", HttpStatus.CONFLICT);
            }
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                confirm.getEmail(), null, AuthorityUtils.createAuthorityList("ROLE_USER"));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (BadCredentialsException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(userByEmail.getEmail());
            token = tokenUtils.generateToken(userDetails);
            userByEmail.setConfirmEmail(null);
            userService.saveUser(userByEmail);
        }
        return new ResponseEntity<>(new JwtResponse(token), HttpStatus.OK);
    }
}
