package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.OrderItem;
import com.restaurant_rest.entity.Product;
import com.restaurant_rest.entity.ShoppingCartItem;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.model.product.ProductShort;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemRequest;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemResponse;
import com.restaurant_rest.model.user.UserShort;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {
    ShoppingCartMapper MAPPER = Mappers.getMapper(ShoppingCartMapper.class);

    ShoppingCartItemResponse cartItemToCartItemResponse(ShoppingCartItem shoppingCartitem);
    List<ShoppingCartItemResponse> cartItemListToCartItemResponseList(List<ShoppingCartItem> shoppingCart);


    default UserShort userToUserShort(User user) {
        return Mappers.getMapper(UserMapper.class).userToUserShort(user);
    }

    default User userShortToUser(UserShort userShort) {
        return Mappers.getMapper(UserMapper.class).userShortToUser(userShort);
    }

    default ProductShort productToProductShort(Product product) {
        return Mappers.getMapper(ProductMapper.class).productToProductShort(product);
    }

    default Product productShortToProduct(ProductShort productShort) {
        return Mappers.getMapper(ProductMapper.class).productShortToProduct(productShort);
    }

    ShoppingCartItem requestCartItemToCartItem(ShoppingCartItemRequest itemRequest);

    List<OrderItem> cartItemListToOrderItemList(List<ShoppingCartItem> shoppingCart);
}
