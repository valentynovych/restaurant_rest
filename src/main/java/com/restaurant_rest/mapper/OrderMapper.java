package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.*;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.order.OrderDetails;
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

    Order orderDetailsToOrder(OrderDetails orderDetails);

    default Address addressRequestToAddress(AddressRequest addressRequest) {
        return Mappers.getMapper(AddressMapper.class).addressRequestToAddress(addressRequest);
    }
}
