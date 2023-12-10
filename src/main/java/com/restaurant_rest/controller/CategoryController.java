package com.restaurant_rest.controller;

import com.restaurant_rest.model.category.MainCategoryShortResponse;
import com.restaurant_rest.model.category.SubcategoryShortResponse;
import com.restaurant_rest.service.MainCategoryService;
import com.restaurant_rest.service.SubcategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
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

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/all-categories")
    public Page<MainCategoryShortResponse> getAllMainCategory(
            @Parameter(example = "0") @Min(value = 0, message = "Min is {value}") @RequestParam int page,
            @Parameter(example = "10") @Min(value = 1, message = "Min is {value}") @RequestParam int pageSize) {
        return mainCategoryService.getAllMainCategories(page, pageSize);
    }

    @Operation(summary = "Get all subcategories by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{\"error\":\"UNAUTHORIZED\"}"))})
    })
    @GetMapping("/subcategories/{mainCategoryId}")
    public Page<SubcategoryShortResponse> getSubcategoriesByMainCategory(
            @Parameter(example = "0") @Min(value = 0, message = "Min is {value}") @RequestParam int page,
            @Parameter(example = "10") @Min(value = 1, message = "Min is {value}") @RequestParam int pageSize,
            @Parameter(example = "1") @Min(value = 1, message = "Min is {value}") @PathVariable Long mainCategoryId) {
        return subcategoryService.getSubcategoriesByMainCategory(mainCategoryId, page, pageSize);
    }
}
