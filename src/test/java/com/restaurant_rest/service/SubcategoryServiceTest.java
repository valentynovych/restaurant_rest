package com.restaurant_rest.service;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.entity.Subcategory;
import com.restaurant_rest.model.category.SubcategoryShortResponse;
import com.restaurant_rest.repositoty.SubcategoryRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubcategoryServiceTest {

    @Mock
    private SubcategoryRepo subcategoryRepo;
    @InjectMocks
    private SubcategoryService subcategoryService;
    List<Subcategory> subcategories;
    @BeforeEach
    void setUp() {
        subcategories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Subcategory subcategory = new Subcategory();
            subcategory.setId((long) i);
            subcategory.setSubcategoryName("subcategory" + 1);
            MainCategory mainCategory = new MainCategory();
            mainCategory.setId(1L);
            subcategory.setParentCategory(mainCategory);
            subcategories.add(subcategory);
        }
    }

    @Test
    void getSubcategoriesByMainCategory_ifResultsIsPresent() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Subcategory> subcategoryPage = new PageImpl<>(subcategories, pageable, subcategories.size());
        when(subcategoryRepo.findAll(
                any(Specification.class), any(Pageable.class)))
                .thenReturn(subcategoryPage);
        Page<SubcategoryShortResponse> byMainCategory =
                subcategoryService.getSubcategoriesByMainCategory(1L, 0, 10);
        Assertions.assertEquals(10, byMainCategory.getContent().size());
    }

    @Test
    void getSubcategoriesByMainCategory_ifResultsIsEmpty() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Subcategory> subcategoryPage = new PageImpl<>(new ArrayList<Subcategory>(), pageable, subcategories.size());
        when(subcategoryRepo.findAll(
                any(Specification.class), any(Pageable.class)))
                .thenReturn(subcategoryPage);
        Page<SubcategoryShortResponse> byMainCategory =
                subcategoryService.getSubcategoriesByMainCategory(1L, 0, 10);
        Assertions.assertEquals(0, byMainCategory.getContent().size());
    }
}