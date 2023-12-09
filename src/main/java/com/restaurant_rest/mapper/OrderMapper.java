package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.Order;
import com.restaurant_rest.entity.OrderItem;
import com.restaurant_rest.entity.Product;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.model.order.OrderItemResponse;
import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.model.order.OrderShortResponse;
import com.restaurant_rest.model.product.ProductShort;
import com.restaurant_rest.model.user.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);

    OrderShortResponse orderToOrderShortResponse(Order order);
    List<OrderShortResponse> listOrderToResponseList(List<Order> orders);

    OrderResponse orderToOrderResponse(Order save);

    default UserProfileResponse userToUserProfileResponse(User user) {
        return UserMapper.MAPPER.userToUserProfile(user);
    }

    default OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem) {
        return OrderItemMapper.MAPPER.orderItemToOrderItemResponse(orderItem);
    }

    default ProductShort productToProductShort(Product product) {
        return Mappers.getMapper(ProductMapper.class).productToProductShort(product);
    }

    default Product productShortToProduct(ProductShort productShort) {
        return Mappers.getMapper(ProductMapper.class).productShortToProduct(productShort);
    }

}
