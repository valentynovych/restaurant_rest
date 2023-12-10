package com.restaurant_rest.controller;

import com.restaurant_rest.model.SimpleError;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.address.AddressResponse;
import com.restaurant_rest.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/addresses")
@Tag(name = "Addresses")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Get address by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = AddressResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SimpleError.class))})
    })
    @GetMapping("/address/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@Parameter(example = "1") @PathVariable Long id) {

        AddressResponse addressById = addressService.getAddressById(id);
        return new ResponseEntity<>(addressById, HttpStatus.OK);

    }

    @Operation(summary = "Get all user addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AddressResponse.class)))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SimpleError.class))})
    })
    @GetMapping("/user-addresses")
    public ResponseEntity<?> getAddressById(Principal principal) {
        List<AddressResponse> userAddresses = addressService.getUserAddresses(principal.getName());
        return new ResponseEntity<>(userAddresses, HttpStatus.OK);
    }

    @Operation(summary = "Add address current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SimpleError.class))})
    })
    @PostMapping("/add")
    public ResponseEntity<?> createUserAddress(Principal principal,
                                               @Valid @RequestBody AddressRequest addressRequest) {
        Long addressId = addressService.createUserAddress(principal.getName(), addressRequest);
        return new ResponseEntity<>(addressId, HttpStatus.CREATED);
    }

    @Operation(summary = "Update user address by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SimpleError.class))})
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserAddress(@Parameter(example = "1") @PathVariable Long id,
                                               @Valid @RequestBody AddressRequest addressRequest) {

        AddressResponse response = addressService.updateUserAddress(id, addressRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete user address by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content()}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SimpleError.class))}),
            @ApiResponse(responseCode = "400", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SimpleError.class))})
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserAddress(@Parameter(example = "1") @PathVariable Long id,
                                               Principal principal) {
        boolean isDeleted = addressService.deleteUserAddress(principal.getName(), id);
        if (isDeleted) {
            return new ResponseEntity<>("Адреса успішно видалена", HttpStatus.OK);
        }
        return new ResponseEntity<>("Помилка під час видалення", HttpStatus.BAD_REQUEST);
    }
}
