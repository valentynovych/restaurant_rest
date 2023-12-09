package com.restaurant_rest.controller;

import com.restaurant_rest.ShoppingCartItemRequestValidator;
import com.restaurant_rest.exception.ForbiddenUpdateException;
import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.model.order.OrderShortResponse;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemRequest;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemResponse;
import com.restaurant_rest.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/get-items")
    public ResponseEntity<?> getShoppingCartItems(Principal principal) {
        List<ShoppingCartItemResponse> cartItemResponses = cartService.getUserCartItems(principal.getName());
        return new ResponseEntity<>(cartItemResponses, HttpStatus.OK);
    }

    @InitBinder(value = "shoppingCartItemRequest")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(requestValidator);
    }

    @PostMapping("/add-item")
    public ResponseEntity<?> addShoppingCartItem(Principal principal,
                                                 @Valid @RequestBody ShoppingCartItemRequest itemRequest,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        List<ShoppingCartItemResponse> response = cartService.addItemToShoppingCart(principal.getName(), itemRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-item/{id}")
    public ResponseEntity<?> deleteShoppingCartItem(Principal principal,
                                                 @Parameter(example = "1") @Min(value = 1) @PathVariable Long id) {
        try {
            boolean isDelete = cartService.deleteShoppingCartItem(principal.getName(), id);
            if (isDelete) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Сталась помилка під час видалення", HttpStatus.BAD_REQUEST);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ForbiddenUpdateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearShoppingCart(Principal principal) {
        try {
            cartService.clearShoppingCart(principal.getName());
            return new ResponseEntity<>("Корзина успішно очищена", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(value = "/change-item-composition/{id}")
    public ResponseEntity<?> changeCompositionItemCart(Principal principal,
                                                       @Parameter(example = "1") @Min(value = 1) @PathVariable Long id,
                                                       @Valid @RequestBody ShoppingCartItemRequest itemRequest) {
        try {
            ShoppingCartItemResponse itemResponse = cartService.changeCompositionItemCart(principal.getName(), id, itemRequest);
            return new ResponseEntity<>(itemResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Сталась помилка під час оновлення", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/create-order")
    public ResponseEntity<?> createOrderFromShoppingCart(Principal principal) {
        try {
            OrderResponse orderFromShoppingCart = cartService.createOrderFromShoppingCart(principal.getName());
            return new ResponseEntity<>(orderFromShoppingCart, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Сталась помилка під час оновлення", HttpStatus.BAD_REQUEST);
        }
    }
}
