package com.restaurant_rest.service;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.mapper.MainCategoryMapper;
import com.restaurant_rest.model.category.MainCategoryShortResponse;
import com.restaurant_rest.repositoty.MainCategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MainCategoryService {

    private final MainCategoryRepo mainCategoryRepo;

    public Page<MainCategoryShortResponse> getAllMainCategories(int page, int pageSize) {
        log.info(String.format("getAllMainCategories() -> start with page: %s, pageSize: %s", page, pageSize));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("topPosition"));
        Page<MainCategory> all = mainCategoryRepo.findAll(pageable);
        List<MainCategoryShortResponse> responseList =
                MainCategoryMapper.MAPPER.categoryListToShortResponseList(all.getContent());
        Page<MainCategoryShortResponse> responsePage = new PageImpl<>(responseList, pageable, all.getTotalElements());
        log.info(String.format("getAllMainCategories() -> exit, return elements: %s", responsePage.getContent().size()));
        return responsePage;
    }
}
