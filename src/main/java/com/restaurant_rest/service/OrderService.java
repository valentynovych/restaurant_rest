package com.restaurant_rest.service;

import com.restaurant_rest.entity.Order;
import com.restaurant_rest.entity.OrderItem;
import com.restaurant_rest.entity.ShoppingCartItem;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.entity.enums.OrderStatus;
import com.restaurant_rest.mapper.OrderMapper;
import com.restaurant_rest.mapper.ShoppingCartMapper;
import com.restaurant_rest.model.order.OrderDetails;
import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.model.order.OrderShortResponse;
import com.restaurant_rest.repositoty.OrderRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
        List<OrderShortResponse> shortResponses = OrderMapper.MAPPER.listOrderToResponseList(orderByUser.getContent());
        Page<OrderShortResponse> responsePage = new PageImpl<>(shortResponses, pageable, orderByUser.getTotalElements());
        log.info(String.format("getUserOrders() -> exit, return page: %s, elements: %s", page, pageSize));
        return responsePage;
    }

    public OrderStatus getOrderStatusById(String username, Long id) {
        log.info(String.format("getOrderStatusById() -> start with username: %s, order id: %s", username, id));
        User userByEmail = userService.getUserByEmail(username);
        Optional<Order> byId = orderRepo.findById(id);
        Order order = byId.orElseThrow(() -> new EntityNotFoundException(String.format(
                "Замовлення з id: %s не знайдено", id)));
        if (order.getUser().getId().equals(userByEmail.getId())) {
            log.info("getOrderStatusById() -> exit, return order status");
            return order.getStatus();
        } else {
            log.error(String.format("getOrderStatusById() -> order with id: %s, does not belong user: %s", id, username));
            throw new EntityNotFoundException(String.format(
                    "Замовлення з id: %s не знайдено", id));
        }
    }

    public OrderResponse createOrderFromShoppingCart(String username, @Valid OrderDetails orderDetails) {
        log.info("createOrderFromShoppingCart() -> start, with username: " + username);
        User userByEmail = userService.getUserByEmail(username);
        List<ShoppingCartItem> shoppingCart = userByEmail.getShoppingCart();
        if (shoppingCart.isEmpty()) {
            log.error("createOrderFromShoppingCart() -> get shopping cart is empty, throw EntityNotFoundException");
            throw new EntityNotFoundException(String.format(
                    "Корзина користувача %s пуста, неможливо сформувати замовлення", username));
        }
        log.info("createOrderFromShoppingCart() -> get shopping cart, his size: " + shoppingCart.size());
        List<OrderItem> orderItems = ShoppingCartMapper.MAPPER.cartItemListToOrderItemList(shoppingCart);

        Order order = OrderMapper.MAPPER.orderDetailsToOrder(orderDetails);
        order.setUser(userByEmail);
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        order.setDatetimeOfCreate(Instant.now());
        order.setTotalAmount(calculateTotalAmountShoppingCart(shoppingCart));
        order.setUsedPromotion(new HashSet<>(userByEmail.getUserPromotion()));
        order.setAccruedBonuses(order.getTotalAmount().intValue());
        order.setStatus(OrderStatus.NEW);

        Order save = orderRepo.save(order);
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToOrderResponse(save);
        log.info("createOrderFromShoppingCart() -> exit, return saved order with id : " + orderResponse.getId());
        return orderResponse;
    }

    private BigDecimal calculateTotalAmountShoppingCart(List<ShoppingCartItem> shoppingCart) {
        log.info("calculateTotalAmountShoppingCart() -> start");
        BigDecimal total = BigDecimal.ZERO;

        for (ShoppingCartItem shoppingCartItem : shoppingCart) {
            if (shoppingCartItem.getItemSalePrice() != null) {
                log.info("calculateTotalAmountShoppingCart() -> add item SALE price to total");
                total = total.add(shoppingCartItem.getItemSalePrice());
            } else {
                log.info("calculateTotalAmountShoppingCart() -> add item price to total");
                total = total.add(shoppingCartItem.getItemPrice());
            }
        }
        log.info("calculateTotalAmountShoppingCart() -> exit, return totalAmount: " + total);
        return total;
    }
}
