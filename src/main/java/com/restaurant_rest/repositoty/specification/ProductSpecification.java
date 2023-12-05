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

        if (productCriteria.isIngredients()) {
            predicates.add(criteriaBuilder.equal(root.get("isIngredient"), true));
        }

        if (productCriteria.isPromotional()) {
            predicates.add(
                    criteriaBuilder.and(criteriaBuilder.equal(root.get("promotionIsActive"), true),
                            criteriaBuilder.equal(root.get("isIngredient"), false)));

        }

        if (productCriteria.isNovelty()) {
            predicates.add(
                    criteriaBuilder.and(criteriaBuilder.equal(root.get("isNovelty"), true),
                            criteriaBuilder.equal(root.get("isIngredient"), false)));
        }

        if (productCriteria.getByCategoryId() != null) {
            MainCategory mainCategory = new MainCategory();
            mainCategory.setId(productCriteria.getByCategoryId());
            predicates.add(
                    criteriaBuilder.and(criteriaBuilder.equal(root.get("mainCategory"), mainCategory),
                            criteriaBuilder.equal(root.get("isIngredient"), false)));
        }

        if (productCriteria.getBySubcategoryId() != null) {
            Subcategory subcategory = new Subcategory();
            subcategory.setId(productCriteria.getBySubcategoryId());
            predicates.add(
                    criteriaBuilder.and(criteriaBuilder.equal(root.get("subcategory"), subcategory),
                            criteriaBuilder.equal(root.get("isIngredient"), false)));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
