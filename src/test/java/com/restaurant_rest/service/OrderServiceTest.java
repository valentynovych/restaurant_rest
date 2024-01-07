package com.restaurant_rest.service;

import com.restaurant_rest.entity.*;
import com.restaurant_rest.entity.enums.OrderStatus;
import com.restaurant_rest.entity.enums.PaymentMethod;
import com.restaurant_rest.mapper.AddressMapper;
import com.restaurant_rest.mapper.ShoppingCartMapper;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.order.OrderDetails;
import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.model.order.OrderShortResponse;
import com.restaurant_rest.repositoty.AddressRepo;
import com.restaurant_rest.repositoty.OrderItemRepo;
import com.restaurant_rest.repositoty.OrderRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;
    @Mock
    private UserService userService;
    @Mock
    private AddressRepo addressRepo;
    @Mock
    private OrderItemRepo orderItemRepo;
    @Mock
    ShoppingCartService shoppingCartService;
    @InjectMocks
    private OrderService orderService;
    private List<ShoppingCartItem> shoppingCartItems;
    private Order order;
    private User user;
    private OrderDetails orderDetails;

    @BeforeEach
    void setUp() {
        shoppingCartItems = new ArrayList<>();
        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        for (int i = 0; i < 10; i++) {
            ShoppingCartItem cartItem = new ShoppingCartItem();
            cartItem.setUser(user);
            cartItem.setItemPrice(new BigDecimal(150));
            shoppingCartItems.add(cartItem);
        }
        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setPayment(PaymentMethod.CASH);
        order.setDatetimeOfCreate(Instant.now());
        order.setTotalAmount(BigDecimal.valueOf(1500));
        order.setOrderItems(new HashSet<>(ShoppingCartMapper.MAPPER.cartItemListToOrderItemList(shoppingCartItems)));
        order.setDeliveryTime(75);
        order.setReservedTime("15:00");
        order.setUsedBonuses(250);

        AddressRequest address = new AddressRequest();
        address.setId(1L);
        address.setAddressName("Address");
        address.setCity("City");
        address.setStreet("Street");
        address.setBuilding("building");

        orderDetails = new OrderDetails();
        orderDetails.setAddress(address);
        orderDetails.setPayment(PaymentMethod.CASH);
        orderDetails.setUsedBonuses(order.getUsedBonuses());
        orderDetails.setDeliveryTime(order.getDeliveryTime());
        orderDetails.setReservedTime(order.getReservedTime());
    }

    @Test
    void getUserOrders_ifUserHasOrders() {
        Pageable pageable = PageRequest.ofSize(10);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        orders.addAll(List.of(new Order(), new Order(), new Order()));
        Page<Order> page = new PageImpl<>(orders, pageable, orders.size());

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(orderRepo.findOrderByUser(user, pageable)).thenReturn(page);

        Page<OrderShortResponse> userOrders = orderService.getUserOrders(user.getUsername(), 0, 10);
        List<OrderShortResponse> content = userOrders.getContent();
        assertFalse(content.isEmpty());
        OrderShortResponse response = content.get(0);
        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getTotalAmount(), response.getTotalAmount());
        assertEquals(order.getPayment(), response.getPayment());
        assertEquals(order.getStatus(), response.getStatus());
    }

    @Test
    void getOrderStatusById_ifOrderIsPresentAndOwnerCurrentUser() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));

        OrderStatus orderStatusById = orderService.getOrderStatusById(user.getUsername(), order.getId());
        assertNotNull(orderStatusById);
        assertEquals(OrderStatus.NEW, orderStatusById);
    }

    @Test
    void getOrderStatusById_ifOrderIsPresentAndNotOwnerCurrentUser() {
        User user1 = new User();
        user1.setId(2L);
        user1.setEmail("user1@gmail.com");
        when(userService.getUserByEmail(user1.getEmail())).thenReturn(user1);
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(EntityNotFoundException.class, () ->
                orderService.getOrderStatusById(user1.getUsername(), order.getId()));
    }

    @Test
    void getOrderStatusById_ifOrderIsEmpty() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(orderRepo.findById(order.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.getOrderStatusById(user.getUsername(), order.getId()));
    }

    @Test
    void createOrderFromShoppingCart_ifShoppingCartIsEmpty() {
        user.setShoppingCart(new ArrayList<>());

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        assertThrows(EntityNotFoundException.class, () ->
                orderService.createOrderFromShoppingCart(user.getUsername(), orderDetails));
    }

    @Test
    void createOrderFromShoppingCart_ifShoppingCartIsPresent() {
        shoppingCartItems.get(2).setItemSalePrice(new BigDecimal(120));
        user.setShoppingCart(shoppingCartItems);
        user.setUserPromotion(new ArrayList<>());
        Address address = AddressMapper.MAPPER.addressRequestToAddress(orderDetails.getAddress());
        List<OrderItem> orderItems = ShoppingCartMapper.MAPPER.cartItemListToOrderItemList(user.getShoppingCart());


        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(addressRepo.save(any(Address.class))).thenReturn(address);
        when(orderItemRepo.saveAll(ArgumentMatchers.<OrderItem>anyList())).thenReturn(orderItems);
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        OrderResponse orderFromShoppingCart = orderService.createOrderFromShoppingCart(user.getUsername(), orderDetails);
        verify(shoppingCartService).clearShoppingCart(user.getUsername());

        assertEquals(order.getUser().getId(), orderFromShoppingCart.getUser().getUserId());
        assertEquals(order.getTotalAmount(), orderFromShoppingCart.getTotalAmount());
        assertEquals(order.getDeliveryTime(), orderFromShoppingCart.getDeliveryTime());
    }
}