package com.restaurant_rest.model.user;

import com.restaurant_rest.model.product.ProductShort;
import lombok.Data;

import java.util.List;

@Data
public class ProductWishListWrap {
    private List<ProductShort> productWishlist;
}
