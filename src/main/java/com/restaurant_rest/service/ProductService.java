package com.restaurant_rest.service;

import com.restaurant_rest.entity.Product;
import com.restaurant_rest.mapper.ProductMapper;
import com.restaurant_rest.model.product.ProductResponse;
import com.restaurant_rest.model.product.ProductCriteria;
import com.restaurant_rest.model.product.ProductShortResponse;
import com.restaurant_rest.repositoty.ProductRepo;
import com.restaurant_rest.repositoty.specification.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepo productRepo;

    public Page<ProductShortResponse> getAllProducts(int page, int pageSize) {
        log.info(String.format("getAllProducts() -> start, with page: %s, pageSize: %s", page, pageSize));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC);
        Page<Product> all = productRepo.findAll(pageable);
        List<ProductShortResponse> responseList =
                ProductMapper.MAPPER.productListToShortResponseList(all.getContent());
        Page<ProductShortResponse> responsePage =
                new PageImpl<>(responseList, pageable, all.getTotalElements());
        log.info(String.format("getAllProducts() -> exit, return elements: %s, totalElement: %s",
                responsePage.getContent().size(), responsePage.getTotalElements()));
        return responsePage;
    }

    public Page<ProductShortResponse> getProductsByCriteria(int page, int pageSize, ProductCriteria productCriteria) {
        log.info(String.format("getProductsByCriteria() -> start, with page: %s, pageSize: %s, productCriteria is: %s",
                page, pageSize, productCriteria));
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> all = productRepo.findAll(Specification.where(
                new ProductSpecification(productCriteria)), pageable);
        List<ProductShortResponse> responseList =
                ProductMapper.MAPPER.productListToShortResponseList(all.getContent());
        Page<ProductShortResponse> responsePage =
                new PageImpl<>(responseList, pageable, all.getTotalElements());
        log.info(String.format("getProductsByCriteria() -> exit, return elements: %s, totalElement: %s",
                responsePage.getContent().size(), responsePage.getTotalElements()));
        return responsePage;
    }

    public ProductResponse getProductById(long productId) {
        log.info(String.format("getProductById() -> start, with id: %s", productId));
        Optional<Product> byId = productRepo.findById(productId);
        Product product = byId.orElseThrow(EntityNotFoundException::new);
        ProductResponse productResponse = ProductMapper.MAPPER.productToProductResponse(product);
        log.info("getProductById() -> exit, return ProductResponse with id: " + productId);
        return productResponse;
    }
}
