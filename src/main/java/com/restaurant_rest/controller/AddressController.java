package com.restaurant_rest.controller;

import com.restaurant_rest.exception.ForbiddenUpdateException;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.address.AddressResponse;
import com.restaurant_rest.service.AddressService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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

    @GetMapping("/address/{id}")
    public ResponseEntity<?> getAddressById(@Parameter(example = "1") @PathVariable Long id) {
        try {
            AddressResponse addressById = addressService.getAddressById(id);
            return new ResponseEntity<>(addressById, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(String.format("Address with id: %s not found", id), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user-addresses")
    public ResponseEntity<?> getAddressById(Principal principal) {
        List<AddressResponse> userAddresses = addressService.getUserAddresses(principal.getName());
        return new ResponseEntity<>(userAddresses, HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<?> createUserAddress(Principal principal,
                                               @Valid @RequestBody AddressRequest addressRequest,
                                               @NotNull BindingResult result) {
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
        }
        Long addressId = addressService.createUserAddress(principal.getName(), addressRequest);
        return new ResponseEntity<>(addressId, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserAddress(@Parameter(example = "1") @PathVariable Long id,
                                               @Valid @RequestBody AddressRequest addressRequest,
                                               @NotNull BindingResult result) {
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
        }
        try {
            AddressResponse response = addressService.updateUserAddress(id, addressRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ForbiddenUpdateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserAddress(@Parameter(example = "1") @PathVariable Long id,
                                               Principal principal) {
        try {
            boolean isDeleted = addressService.deleteUserAddress(principal.getName(), id);
            if (isDeleted) {
                return new ResponseEntity<>("Адреса успішно видалена", HttpStatus.OK);
            }
            return new ResponseEntity<>("Помилка під час видалення", HttpStatus.OK);
        } catch (ForbiddenUpdateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(String.format("Адреси з id: %s не існує", id), HttpStatus.NOT_FOUND);

        }
    }


}
