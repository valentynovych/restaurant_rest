package com.restaurant_rest.service;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.model.category.MainCategoryShortResponse;
import com.restaurant_rest.repositoty.MainCategoryRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainCategoryServiceTest {

    @Mock
    private MainCategoryRepo categoryRepo;
    @InjectMocks
    private MainCategoryService mainCategoryService;
    List<MainCategory> mainCategories;

    @BeforeEach
    void setUp() {
        mainCategoryService = new MainCategoryService(categoryRepo);
        mainCategories = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MainCategory category = new MainCategory();
            category.setId(1L);
            category.setCategoryName("Category_" + i);
            mainCategories.add(category);
        }
    }

    @Test
    void getAllMainCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MainCategory> categoryPage = new PageImpl<>(mainCategories, pageable, mainCategories.size());
        when(categoryRepo.findAll(any(Pageable.class))).thenReturn(categoryPage);
        Page<MainCategoryShortResponse> allMainCategories =
                mainCategoryService.getAllMainCategories(pageable.getPageNumber(), pageable.getPageSize());
        List<MainCategoryShortResponse> content = allMainCategories.getContent();
        assertFalse(content.isEmpty());
        assertEquals(mainCategories.size(), content.size());
        for (int i = 0; i < mainCategories.size(); i++) {
            MainCategory category = mainCategories.get(i);
            MainCategoryShortResponse mainCategoryShort = content.get(i);
            assertEquals(category.getId(), mainCategoryShort.getId());
            assertEquals(category.getCategoryName(), mainCategoryShort.getName());
        }


    }
}