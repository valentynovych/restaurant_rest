package com.restaurant_rest.controller;

import com.restaurant_rest.ShoppingCartItemRequestValidator;
import com.restaurant_rest.model.SimpleError;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemRequest;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemResponse;
import com.restaurant_rest.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@Tag(name = "ShoppingCart")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService cartService;
    private final ShoppingCartItemRequestValidator requestValidator;

    @Operation(summary = "Get all ShoppingCart items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ShoppingCartItemResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/get-items")
    public ResponseEntity<?> getShoppingCartItems(Principal principal) {

        List<ShoppingCartItemResponse> cartItemResponses = cartService.getUserCartItems(principal.getName());
        return new ResponseEntity<>(cartItemResponses, HttpStatus.OK);
    }

    @InitBinder(value = "shoppingCartItemRequest")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(requestValidator);
    }

    @Operation(summary = "Add item to ShoppingCart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ShoppingCartItemResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @PostMapping("/add-item")
    public ResponseEntity<?> addShoppingCartItem(Principal principal,
                                                 @Valid @RequestBody ShoppingCartItemRequest itemRequest) {

        List<ShoppingCartItemResponse> response = cartService.addItemToShoppingCart(principal.getName(), itemRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete item by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "Елемент успішно видалений"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @DeleteMapping("/delete-item/{id}")
    public ResponseEntity<?> deleteShoppingCartItem(Principal principal,
                                                    @Parameter(example = "1") @Min(1) @PathVariable Long id) {
        boolean isDelete = cartService.deleteShoppingCartItem(principal.getName(), id);
        if (isDelete) {
            return new ResponseEntity<>("Елемент успішно видалений",HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new SimpleError("Сталась помилка під час видалення"), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete all ShoppingCart items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "Корзина успішно очищена"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearShoppingCart(Principal principal) {

        cartService.clearShoppingCart(principal.getName());
        return new ResponseEntity<>("Корзина успішно очищена", HttpStatus.OK);
    }

    @Operation(summary = "Change ShoppingCart item by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShoppingCartItemResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @PatchMapping(value = "/change-item-composition/{id}")
    public ResponseEntity<?> changeCompositionItemCart(Principal principal,
                                                       @Parameter(example = "1") @Min(1) @PathVariable Long id,
                                                       @Valid @RequestBody ShoppingCartItemRequest itemRequest) {

        ShoppingCartItemResponse itemResponse = cartService.changeCompositionItemCart(principal.getName(), id, itemRequest);
        return new ResponseEntity<>(itemResponse, HttpStatus.OK);
    }
}
