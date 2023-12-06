package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.Order;
import com.restaurant_rest.model.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);

    OrderResponse orderToOrderResponse(Order order);
    List<OrderResponse> listOrderToResponseList(List<Order> orders);
}
