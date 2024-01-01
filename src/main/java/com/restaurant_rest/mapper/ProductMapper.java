package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.entity.Product;
import com.restaurant_rest.entity.Subcategory;
import com.restaurant_rest.model.category.MainCategoryShortResponse;
import com.restaurant_rest.model.category.SubcategoryShortResponse;
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

    List<Product> productShortListToProductList(List<ProductShort> productWishlist);

    List<ProductShort> productListToProductShortList(List<Product> productWishlist);

    default MainCategoryShortResponse mainCategoryToMainCategoryShortResponse(MainCategory mainCategory) {
        return Mappers.getMapper(MainCategoryMapper.class).categoryToShortResponse(mainCategory);
    }

    default SubcategoryShortResponse subcategoryToSubcategoryShortResponse(Subcategory subcategory) {
        return Mappers.getMapper(SubcategoryMapper.class).subcategoryToShortResponse(subcategory);
    }
}
