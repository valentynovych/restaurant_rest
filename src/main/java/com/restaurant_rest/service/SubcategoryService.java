package com.restaurant_rest.service;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.entity.Subcategory;
import com.restaurant_rest.mapper.SubcategoryMapper;
import com.restaurant_rest.model.category.SubcategoryShortResponse;
import com.restaurant_rest.repositoty.SubcategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubcategoryService {

    private final SubcategoryRepo subcategoryRepo;

    public Page<SubcategoryShortResponse> getSubcategoriesByMainCategory(Long mainCategoryId, int page, int pageSize) {
        log.info(String.format("getSubcategoriesByMainCategory() -> " +
                "start with page: %s, pageSize: %s, mainCategoryId: %s", page, pageSize, mainCategoryId));
        Pageable pageable = PageRequest.of(page, pageSize);
        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(mainCategoryId);
        Page<Subcategory> all = subcategoryRepo.findAll(
                Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("parentCategory"), mainCategory)), pageable);
        List<SubcategoryShortResponse> responseList =
                SubcategoryMapper.MAPPER.subcategoryListToShortResponseList(all.getContent());
        Page<SubcategoryShortResponse> responsePage = new PageImpl<>(responseList, pageable, all.getTotalElements());
        log.info(String.format("getSubcategoriesByMainCategory() -> " +
                "exit, return elements: %s", responsePage.getContent().size()));
        return responsePage;
    }
}
