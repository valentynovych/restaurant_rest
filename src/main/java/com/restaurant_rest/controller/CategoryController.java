package com.restaurant_rest.controller;

import com.restaurant_rest.model.category.MainCategoryShortResponse;
import com.restaurant_rest.model.category.SubcategoryShortResponse;
import com.restaurant_rest.service.MainCategoryService;
import com.restaurant_rest.service.SubcategoryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CategoryController {

    private final MainCategoryService mainCategoryService;
    private final SubcategoryService subcategoryService;

    @GetMapping("/all-categories")
    public Page<MainCategoryShortResponse> getAllMainCategory(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize) {
        return mainCategoryService.getAllMainCategories(page, pageSize);
    }

    @GetMapping("/subcategories/{mainCategoryId}")
    public Page<SubcategoryShortResponse> getSubcategoriesByMainCategory(
            @Parameter(example = "0") @RequestParam int page,
            @Parameter(example = "10") @RequestParam int pageSize,
            @Parameter(example = "1") @PathVariable Long mainCategoryId) {
        return subcategoryService.getSubcategoriesByMainCategory(mainCategoryId, page, pageSize);
    }
}
