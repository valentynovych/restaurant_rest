package com.restaurant_rest.controller;

import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            description = "Return orders by current user by page and pageSize",
            summary = "Get user orders")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"
            ),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content()),
            @ApiResponse(description = "User Not Found", responseCode = "404", content = @Content())})
    @GetMapping("/user-orders")
    public Page<OrderResponse> getUserOrders(Principal principal,
                                             @Parameter(description = "int value, page number",
                                                     example = "0") @RequestParam int page,
                                             @Parameter(description = "int value, page size",
                                                     example = "10") @RequestParam int pageSize) {
        return orderService.getUserOrders(principal.getName(), page, pageSize);
    }
}
