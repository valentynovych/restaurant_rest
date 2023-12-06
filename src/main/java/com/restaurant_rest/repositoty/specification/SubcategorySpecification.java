package com.restaurant_rest.repositoty.specification;


import com.restaurant_rest.entity.Subcategory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SubcategorySpecification implements Specification<Subcategory> {

    private SearchCriteria searchCriteria;
    @Override
    public Predicate toPredicate(Root<Subcategory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        return null;
    }
}
