package com.restaurant_rest.mapper;

import com.restaurant_rest.entity.OrderItem;
import com.restaurant_rest.model.order.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemMapper MAPPER = Mappers.getMapper(OrderItemMapper.class);

    OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem);
}
