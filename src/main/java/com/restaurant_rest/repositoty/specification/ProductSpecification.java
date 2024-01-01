package com.restaurant_rest.repositoty.specification;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.entity.Product;
import com.restaurant_rest.entity.Subcategory;
import com.restaurant_rest.model.product.ProductCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProductSpecification implements Specification<Product> {

    private final ProductCriteria productCriteria;

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("isIngredient"), productCriteria.isIngredients()));

        if (productCriteria.isIngredients() && productCriteria.getByCategoryId() != null) {
            MainCategory mainCategory = new MainCategory();
            mainCategory.setId(productCriteria.getByCategoryId());
            predicates.add(criteriaBuilder.equal(root.get("forMainCategory"), mainCategory));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }

        if (productCriteria.isPromotional()) {
            predicates.add(criteriaBuilder.equal(root.get("promotionIsActive"), true));
        }

        if (productCriteria.isNovelty()) {
            predicates.add(criteriaBuilder.equal(root.get("isNovelty"), true));
        }

        if (productCriteria.getByCategoryId() != null) {
            MainCategory mainCategory = new MainCategory();
            mainCategory.setId(productCriteria.getByCategoryId());
            predicates.add(criteriaBuilder.equal(root.get("mainCategory"), mainCategory));
        }

        if (productCriteria.getBySubcategoryId() != null) {
            Subcategory subcategory = new Subcategory();
            subcategory.setId(productCriteria.getBySubcategoryId());
            predicates.add(criteriaBuilder.equal(root.get("subcategory"), subcategory));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
