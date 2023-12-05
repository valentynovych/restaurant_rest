package com.restaurant_rest.controller;

import com.restaurant_rest.model.user.UserProfileResponse;
import com.restaurant_rest.model.user.UserProfileRequest;
import com.restaurant_rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @Operation(
            description = "Get endpoint",
            summary = "Get profile current user",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "User Not Found", responseCode = "404")
            })
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserById(Principal principal) {
        UserProfileResponse userProfileResponse;
        try {
            userProfileResponse = userService.getUserProfile(principal.getName());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
    }

    @Operation(
            description = "Update user profile",
            summary = "Update user profile")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content()),
            @ApiResponse(description = "User Not Found", responseCode = "404", content = @Content())})
    @PostMapping("/user/profile")
    public ResponseEntity<?> updateUserProfile(Principal principal,
                                               @Valid @RequestBody UserProfileRequest profileRequest,
                                               BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.updateUserProfile(principal.getName(), profileRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
