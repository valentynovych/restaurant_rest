package com.restaurant_rest.repositoty;

import com.restaurant_rest.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubcategoryRepo extends JpaRepository<Subcategory, Long>, JpaSpecificationExecutor<Subcategory> {
}
