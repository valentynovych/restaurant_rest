package com.restaurant_rest.controller;

import com.restaurant_rest.model.user.ProductWishListWrap;
import com.restaurant_rest.model.user.UserProfileRequest;
import com.restaurant_rest.model.user.UserProfileResponse;
import com.restaurant_rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/user/profile")
    public ResponseEntity<UserProfileResponse> getUserById(Principal principal) {
        UserProfileResponse userProfileResponse = userService.getUserProfile(principal.getName());
        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
    }

    @Operation(summary = "Update user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @PatchMapping("/user/profile")
    public ResponseEntity<?> updateUserProfile(Principal principal,
                                               @Valid @RequestBody UserProfileRequest profileRequest) {
        UserProfileResponse userProfileResponse = userService.updateUserProfile(principal.getName(), profileRequest);
        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
    }

    @Operation(summary = "Update user product wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductWishListWrap.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @PatchMapping("/user/update-wishlist")
    public ResponseEntity<?> updateUserProductWishList(Principal principal,
                                               @Valid @RequestBody ProductWishListWrap productWishList) {
        ProductWishListWrap wishList = userService.updateUserProductWishList(principal.getName(), productWishList);
        return new ResponseEntity<>(wishList, HttpStatus.OK);
    }
}
