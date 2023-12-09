package com.restaurant_rest.service;

import com.restaurant_rest.entity.Order;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.mapper.OrderMapper;
import com.restaurant_rest.model.order.OrderShortResponse;
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

    public Page<OrderShortResponse> getUserOrders(String username, int page, int pageSize) {
        log.info(String.format("getUserOrders() -> start with page: %s, pageSize: %s", page, pageSize));
        Pageable pageable = PageRequest.of(page, pageSize);
        User user = userService.getUserByEmail(username);
        Page<Order> orderByUser = orderRepo.findOrderByUser(user, pageable);
        List<OrderShortResponse> orderShortRespons = OrderMapper.MAPPER.listOrderToResponseList(orderByUser.getContent());
        Page<OrderShortResponse> responsePage = new PageImpl<>(orderShortRespons, pageable, orderByUser.getTotalElements());
        log.info(String.format("getUserOrders() -> exit, return page: %s, elements: %s", page, pageSize));
        return responsePage;
    }
}
