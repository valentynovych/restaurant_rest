package com.restaurant_rest.controller;

import com.restaurant_rest.model.product.ProductCriteria;
import com.restaurant_rest.model.product.ProductResponse;
import com.restaurant_rest.model.product.ProductShortResponse;
import com.restaurant_rest.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/all-products")
    public Page<ProductShortResponse> getAllProducts(
            @Parameter(example = "0") @Min(0) @RequestParam int page,
            @Parameter(example = "10") @Min(1) @RequestParam int pageSize) {
        return productService.getAllProducts(page, pageSize);
    }

    @Operation(summary = "Get all novelty products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/novelty")
    public Page<ProductShortResponse> getNoveltyProducts(
            @Parameter(example = "0") @Min(0) @RequestParam int page,
            @Parameter(example = "10") @Min(1) @RequestParam int pageSize) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .isNovelty(true)
                                .isIngredients(false)
                                .build());
    }

    @Operation(summary = "Get all promotional products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/promotional")
    public Page<ProductShortResponse> getPromotionalProducts(
            @Parameter(example = "0") @Min(0) @RequestParam int page,
            @Parameter(example = "10") @Min(1) @RequestParam int pageSize) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .isPromotional(true)
                                .isIngredients(false)
                                .build());
    }

    @Operation(summary = "Get products by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/by-category/{categoryId}")
    public Page<ProductShortResponse> getProductsByCategory(
            @Parameter(example = "0") @Min(0) @RequestParam int page,
            @Parameter(example = "10") @Min(1) @RequestParam int pageSize,
            @Parameter(example = "1") @Min(1) @PathVariable long categoryId) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .byCategoryId(categoryId)
                                .isIngredients(false)
                                .build());
    }

    @Operation(summary = "Get products by subcategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/by-subcategory/{subcategoryId}")
    public Page<ProductShortResponse> getProductsBySubcategory(
            @Parameter(example = "0") @Min(0) @RequestParam int page,
            @Parameter(example = "10") @Min(1) @RequestParam int pageSize,
            @Parameter(example = "1") @Min(1) @PathVariable long subcategoryId) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .bySubcategoryId(subcategoryId)
                                .isIngredients(false)
                                .build());
    }

    @Operation(summary = "Get ingredients by forCategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/ingredients-for-category/{mainCategoryId}")
    public Page<ProductShortResponse> getIngredientByForMainCategory(
            @Parameter(example = "0") @Min(0) @RequestParam int page,
            @Parameter(example = "10") @Min(1) @RequestParam int pageSize,
            @Parameter(example = "1") @Min(1) @PathVariable long mainCategoryId) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .isIngredients(true)
                                .byCategoryId(mainCategoryId)
                                .build());
    }

    @Operation(summary = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ProductResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductById(@Parameter(example = "1") @Min(1) @PathVariable long id) {
        ProductResponse productById = productService.getProductById(id);
        return new ResponseEntity<>(productById, HttpStatus.OK);
    }

}
