package com.restaurant_rest.controller;

import com.restaurant_rest.exception.RefreshTokenException;
import com.restaurant_rest.model.CustomError;
import com.restaurant_rest.model.SimpleError;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler({RefreshTokenException.class})
    public ResponseEntity<?> handleTokenRefreshException(RefreshTokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AuthenticationException.class})
    public void handleTokenAccessException(Exception ex) {
        System.out.println(ex.getMessage());
//        return new ResponseEntity<>(new SimpleError(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SimpleError.class))})
    })
    @ExceptionHandler(value = {JwtException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleTokenAccessException(JwtException ex) {
        return new ResponseEntity<>(new SimpleError(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SimpleError.class))})
    })
    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(new SimpleError(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Endpoint for handle MethodArgumentNotValidException
     * when body object field not valid
     * @return List<CustomError>
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema =
                    @Schema(implementation = CustomError.class)))})
    })
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
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

    /**
     * Endpoint for handle HandlerMethodValidationException
     * when headers parameters not valid
     * @return List<CustomError>
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema =
                    @Schema(implementation = CustomError.class)))})
    })
    @ExceptionHandler(value = HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleHeadersValidationException(HandlerMethodValidationException ex) {
        List<ParameterValidationResult> allValidationResults = ex.getAllValidationResults();
        List<CustomError> customErrors = allValidationResults
                .stream()
                .map(fieldError -> new CustomError(
                        "Header parameter",
                        fieldError.getMethodParameter().getParameterName(),
                        fieldError.getResolvableErrors().get(0).getDefaultMessage(),
                        fieldError.getArgument()))
                .toList();
        return new ResponseEntity<>(customErrors, HttpStatus.BAD_REQUEST);
    }
}
