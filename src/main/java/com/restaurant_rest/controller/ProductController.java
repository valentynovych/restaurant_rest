package com.restaurant_rest.controller;

import com.restaurant_rest.model.product.ProductCriteria;
import com.restaurant_rest.model.product.ProductResponse;
import com.restaurant_rest.model.product.ProductShortResponse;
import com.restaurant_rest.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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

    @GetMapping("/all-products")
    public Page<ProductShortResponse> getAllProducts(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize) {
        return productService.getAllProducts(page, pageSize);
    }

    @GetMapping("/novelty")
    public Page<ProductShortResponse> getNoveltyProducts(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .isNovelty(true)
                                .isIngredients(false)
                                .build());
    }

    @GetMapping("/promotional")
    public Page<ProductShortResponse> getPromotionalProducts(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .isPromotional(true)
                                .isIngredients(false)
                                .build());
    }

    @GetMapping("/by-category/{categoryId}")
    public Page<ProductShortResponse> getProductsByCategory(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize,
            @Parameter(example = "1") @PathVariable long categoryId) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .byCategoryId(categoryId)
                                .isIngredients(false)
                                .build());
    }

    @GetMapping("/by-subcategory/{subcategoryId}")
    public Page<ProductShortResponse> getProductsBySubcategory(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize,
            @Parameter(example = "1") @PathVariable long subcategoryId) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .bySubcategoryId(subcategoryId)
                                .isIngredients(false)
                                .build());
    }


    @GetMapping("/ingredients")
    public Page<ProductShortResponse> getProductsIsIngredient(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .isIngredients(true)
                                .build());
    }

    @GetMapping("/ingredients-for/{mainCategoryId}")
    public Page<ProductShortResponse> getIngredientByForMainCategory(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize,
            @Parameter(example = "1") @PathVariable long mainCategoryId) {
        return productService
                .getProductsByCriteria(
                        page,
                        pageSize,
                        ProductCriteria.builder()
                                .isIngredients(true)
                                .byCategoryId(mainCategoryId)
                                .build());
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductById(@Parameter(example = "1") @PathVariable long id) {
        try {
            ProductResponse productById = productService.getProductById(id);
            return new ResponseEntity<>(productById, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
