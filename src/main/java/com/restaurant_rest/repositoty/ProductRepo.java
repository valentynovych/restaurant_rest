package com.restaurant_rest.repositoty;

import com.restaurant_rest.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
//    @Query(value = "SELECT p from Product p WHERE p.id IN :ids")
    List<Product> findByIdIn(List<Long> ids);
}
