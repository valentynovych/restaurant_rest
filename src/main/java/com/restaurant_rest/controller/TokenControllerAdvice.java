package com.restaurant_rest.controller;

import com.restaurant_rest.model.CustomError;
import com.restaurant_rest.model.SimpleError;
import com.restaurant_rest.model.order.OrderResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TokenControllerAdvice {

//    @ExceptionHandler(value = RefreshTokenException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ResponseEntity<?> handleTokenRefreshException(RefreshTokenException ex, WebRequest request) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
//    }

    @ExceptionHandler(value = JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleTokenAccessException(ExpiredJwtException ex) {
        return new ResponseEntity<>(new SimpleError(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SimpleError.class))})
    })
    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleTokenRefreshException(EntityNotFoundException ex) {
        return new ResponseEntity<>(new SimpleError(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema =
                    @Schema(implementation = CustomError.class)))})
    })
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, BindingResult result) {
        List<CustomError> customErrors = result.getFieldErrors()
                .stream()
                .map(fieldError -> new CustomError(
                        fieldError.getObjectName(),
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue()))
                .toList();
        return new ResponseEntity<>(customErrors, HttpStatus.BAD_REQUEST);
    }
}
