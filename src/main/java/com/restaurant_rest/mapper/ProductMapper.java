package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.Product;
import com.restaurant_rest.model.product.ProductResponse;
import com.restaurant_rest.model.product.ProductShort;
import com.restaurant_rest.model.product.ProductShortResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    ProductShortResponse productToShortResponse(Product product);

    List<ProductShortResponse> productListToShortResponseList(List<Product> products);

    ProductResponse productToProductResponse(Product product);

    ProductShort productToProductShort(Product product);

    Product productShortToProduct(ProductShort productShort);
}
