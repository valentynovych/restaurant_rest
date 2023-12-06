package com.restaurant_rest.repositoty;

import com.restaurant_rest.entity.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MainCategoryRepo extends JpaRepository<MainCategory, Long>, JpaSpecificationExecutor<MainCategory> {
}
