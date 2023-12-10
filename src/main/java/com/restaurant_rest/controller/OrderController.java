package com.restaurant_rest.controller;

import com.restaurant_rest.entity.enums.OrderStatus;
import com.restaurant_rest.model.CustomError;
import com.restaurant_rest.model.SimpleError;
import com.restaurant_rest.model.order.OrderDetails;
import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.model.order.OrderShortResponse;
import com.restaurant_rest.model.order.OrderStatusResponse;
import com.restaurant_rest.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create order from shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = OrderResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SimpleError.class))})
    })
    @GetMapping("/user-orders")
    public Page<OrderShortResponse> getUserOrders(Principal principal,
                                                  @Parameter(example = "0") @RequestParam int page,
                                                  @Parameter(example = "10") @RequestParam int pageSize) {
        return orderService.getUserOrders(principal.getName(), page, pageSize);
    }

    @Operation(summary = "Get order status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = OrderStatusResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SimpleError.class))}),
    })
    @GetMapping("/status/{id}")
    public ResponseEntity<?> getOrderStatus(Principal principal,
                                            @Parameter(example = "1") @PathVariable Long id) {
        OrderStatus orderStatusById = orderService.getOrderStatusById(principal.getName(), id);
        return new ResponseEntity<>(OrderStatusResponse.builder().status(orderStatusById).build(), HttpStatus.OK);
    }

    @Operation(summary = "Create order from shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = OrderResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SimpleError.class))})
    })
    @PostMapping(value = "/create")
    public ResponseEntity<?> createOrderFromShoppingCart(Principal principal,
                                                         @Valid @RequestBody OrderDetails orderDetails) {
        OrderResponse orderFromShoppingCart = orderService.createOrderFromShoppingCart(principal.getName(), orderDetails);
        return new ResponseEntity<>(orderFromShoppingCart, HttpStatus.CREATED);
    }
}
