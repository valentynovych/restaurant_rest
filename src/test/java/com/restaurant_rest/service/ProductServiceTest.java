package com.restaurant_rest.service;

import com.restaurant_rest.entity.Product;
import com.restaurant_rest.model.product.ProductCriteria;
import com.restaurant_rest.model.product.ProductResponse;
import com.restaurant_rest.model.product.ProductShortResponse;
import com.restaurant_rest.repositoty.ProductRepo;
import com.restaurant_rest.repositoty.specification.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;
    @InjectMocks
    private ProductService productService;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setId(1L);
            product.setName("product_" + i);
            products.add(product);
        }
    }

    @Test
    void getAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(productRepo.findAll(any(ProductSpecification.class), any(Pageable.class))).thenReturn(productPage);
        Page<ProductShortResponse> allProducts =
                productService.getAllProducts(pageable.getPageNumber(), pageable.getPageSize());
        List<ProductShortResponse> content = allProducts.getContent();
        assertEquals(10, content.size());
        for (int i = 0; i < 10; i++) {
            ProductShortResponse productShortResponse = content.get(i);
            Product product = products.get(i);
            assertEquals(productShortResponse.getId(), product.getId());
            assertEquals(productShortResponse.getName(), product.getName());
        }
    }

    @Test
    void getProductsByCriteria() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        ProductCriteria productCriteria = ProductCriteria.builder().build();
        when(productRepo.findAll(any(ProductSpecification.class), any(Pageable.class))).thenReturn(productPage);
        Page<ProductShortResponse> allProducts =
                productService.getProductsByCriteria(
                        pageable.getPageNumber(), pageable.getPageSize(), productCriteria);
        List<ProductShortResponse> content = allProducts.getContent();
        assertEquals(10, content.size());
        for (int i = 0; i < 10; i++) {
            ProductShortResponse productShortResponse = content.get(i);
            Product product = products.get(i);
            assertEquals(productShortResponse.getId(), product.getId());
            assertEquals(productShortResponse.getName(), product.getName());
        }
    }

    @Test
    void getProductById_ifProductIsPresent() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        ProductResponse productById = productService.getProductById(product.getId());
        assertEquals(product.getId(), productById.getId());
        assertEquals(product.getName(), productById.getName());
    }

    @Test
    void getProductById_ifProductIsEmpty() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        when(productRepo.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(product.getId()));
    }
}