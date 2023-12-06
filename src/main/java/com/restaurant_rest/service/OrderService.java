package com.restaurant_rest.service;

import com.restaurant_rest.entity.Order;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.mapper.OrderMapper;
import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.repositoty.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserService userService;

    public Page<OrderResponse> getUserOrders(String username, int page, int pageSize) {
        log.info(String.format("getUserOrders() -> start with page: %s, pageSize: %s", page, pageSize));
        Pageable pageable = PageRequest.of(page, pageSize);
        User user = userService.getUserByEmail(username);
        Page<Order> orderByUser = orderRepo.findOrderByUser(user, pageable);
        List<OrderResponse> orderResponses = OrderMapper.MAPPER.listOrderToResponseList(orderByUser.getContent());
        Page<OrderResponse> responsePage = new PageImpl<>(orderResponses, pageable, orderByUser.getTotalElements());
        log.info(String.format("getUserOrders() -> exit, return page: %s, elements: %s", page, pageSize));
        return responsePage;
    }
}
