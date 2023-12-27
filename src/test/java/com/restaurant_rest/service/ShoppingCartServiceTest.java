package com.restaurant_rest.service;

import com.restaurant_rest.entity.*;
import com.restaurant_rest.entity.enums.OrderStatus;
import com.restaurant_rest.entity.enums.PaymentMethod;
import com.restaurant_rest.entity.enums.PromotionCondition;
import com.restaurant_rest.entity.enums.PromotionType;
import com.restaurant_rest.mapper.ShoppingCartMapper;
import com.restaurant_rest.model.address.AddressRequest;
import com.restaurant_rest.model.order.OrderDetails;
import com.restaurant_rest.model.product.ProductResponse;
import com.restaurant_rest.model.product.ProductShort;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemRequest;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemResponse;
import com.restaurant_rest.repositoty.ShoppingCartItemRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {


    @Mock
    private ProductService productService;
    @Mock
    private UserService userService;
    @Mock
    private ShoppingCartItemRepo cartItemRepo;
    @InjectMocks
    private ShoppingCartService cartService;
    private List<ShoppingCartItem> shoppingCartItems;
    private Order order;
    private User user;
    private OrderDetails orderDetails;
    private ProductShort productShort;
    private ProductShort additionalProduct;
    private ShoppingCartItemRequest itemRequest;
    private ProductResponse productResponse;
    private ProductResponse addProduct;

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

        productShort = new ProductShort();
        productShort.setId(1L);
        productShort.setName("Product1");


        additionalProduct = new ProductShort();
        additionalProduct.setId(2L);
        additionalProduct.setName("Add ingredient");


        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(1L);
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);
        mainCategory.setSubcategories(List.of(subcategory));

        itemRequest = new ShoppingCartItemRequest();
        itemRequest.setProduct(productShort);
        itemRequest.setAdditionalIngredients(List.of(additionalProduct));
        itemRequest.setExclusionIngredients(new ArrayList<>());


        productResponse = new ProductResponse();
        productResponse.setId(productShort.getId());
        productResponse.setName(productShort.getName());
        productResponse.setPrice(BigDecimal.valueOf(150));


        addProduct = new ProductResponse();
        addProduct.setId(additionalProduct.getId());
        addProduct.setName(additionalProduct.getName());
        addProduct.setPrice(BigDecimal.valueOf(50));
    }

    @Test
    void getUserCartItems() {
        user.setShoppingCart(shoppingCartItems);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        List<ShoppingCartItemResponse> userCartItems = cartService.getUserCartItems(user.getUsername());
        assertFalse(userCartItems.isEmpty());
        assertEquals(shoppingCartItems.size(), userCartItems.size());
    }

    @Test
    void addItemToShoppingCart_ifUserDontHavePromotion() {


        user.setShoppingCart(shoppingCartItems);
        user.setUserPromotion(new ArrayList<>());

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));
        shoppingCartItems.add(toCartItem);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }

    }

    @Test
    void addItemToShoppingCart_ifUserHavePromotionCondition_PERCENT_FOR_CATEGORY() {

        user.setUserPromotion(new ArrayList<>());
        Promotion promotion = new Promotion();
        promotion.setPromotionCondition(PromotionCondition.PERCENT_FOR_CATEGORY);
        promotion.setIsActive(Boolean.TRUE);
        user.setUserPromotion(List.of(promotion));

        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(1L);
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);
        mainCategory.setSubcategories(List.of(subcategory));

        Product product = new Product();
        product.setId(productShort.getId());
        product.setName(productShort.getName());
        product.setPrice(BigDecimal.valueOf(120));
        product.setMainCategory(mainCategory);
        product.setSubcategory(subcategory);

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setProduct(product);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));

        promotion.setForCategory(mainCategory);
        promotion.setSubcategory(subcategory);
        promotion.setDiscountAmount(15);

        shoppingCartItems = List.of(toCartItem);
        user.setShoppingCart(shoppingCartItems);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }

    }

    @Test
    void addItemToShoppingCart_ifUserHavePromotionCondition_PERCENT_FOR_PRODUCT() {

        user.setUserPromotion(new ArrayList<>());
        Promotion promotion = new Promotion();
        promotion.setPromotionCondition(PromotionCondition.PERCENT_FOR_PRODUCT);
        promotion.setIsActive(Boolean.TRUE);
        user.setUserPromotion(List.of(promotion));

        Product product = new Product();
        product.setId(productShort.getId());
        product.setName(productShort.getName());
        product.setPrice(BigDecimal.valueOf(120));

        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setProduct(product);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));

        shoppingCartItems = List.of(toCartItem);
        user.setShoppingCart(shoppingCartItems);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }

    }

    @Test
    void addItemToShoppingCart_ifUserHavePromotionCondition_PERCENT_OF_AMOUNT() {

        user.setUserPromotion(new ArrayList<>());
        Promotion promotion = new Promotion();
        promotion.setPromotionCondition(PromotionCondition.PERCENT_OF_AMOUNT);
        promotion.setIsActive(Boolean.TRUE);
        user.setUserPromotion(List.of(promotion));

        Product product = new Product();
        product.setId(productShort.getId());
        product.setName(productShort.getName());
        product.setPrice(BigDecimal.valueOf(120));

        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);
        promotion.setMinimalAmount(100);

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setProduct(product);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));
        toCartItem.setItemSalePrice(BigDecimal.valueOf(150));

        ShoppingCartItem toCartItem1 = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem1.setProduct(product);
        toCartItem1.setItemPrice(BigDecimal.valueOf(200));

        shoppingCartItems = List.of(toCartItem, toCartItem1);
        user.setShoppingCart(shoppingCartItems);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }

    }

    @Test
    void addItemToShoppingCart_ifUserHavePromotionCondition_PERCENT_ON_BIRTHDAY() {

        Promotion promotion = new Promotion();
        promotion.setPromotionCondition(PromotionCondition.PERCENT_ON_BIRTHDAY);
        promotion.setIsActive(Boolean.TRUE);
        user.setUserPromotion(List.of(promotion));
        UserDetails userDetails = new UserDetails();
        userDetails.setDateOfBirth(new Date());
        user.setUserDetails(userDetails);

        Product product = new Product();
        product.setId(productShort.getId());
        product.setName(productShort.getName());
        product.setPrice(BigDecimal.valueOf(120));

        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setProduct(product);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));
        toCartItem.setUser(user);

        shoppingCartItems = List.of(toCartItem);
        user.setShoppingCart(shoppingCartItems);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }

    }

    @Test
    void addItemToShoppingCart_ifUserHavePromotionCondition_FIRST_BUY() {

        Promotion promotion = new Promotion();
        promotion.setPromotionCondition(PromotionCondition.FIRST_BUY);
        promotion.setIsActive(Boolean.TRUE);
        user.setUserPromotion(List.of(promotion));
        user.setTotalOrders(0);

        Product product = new Product();
        product.setId(productShort.getId());
        product.setName(productShort.getName());
        product.setPrice(BigDecimal.valueOf(120));

        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setProduct(product);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));
        toCartItem.setUser(user);

        shoppingCartItems = List.of(toCartItem);
        user.setShoppingCart(shoppingCartItems);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }

    }

    @Test
    void addItemToShoppingCart_ifUserHavePromotionCondition_THIRD_PRODUCT_ON_GIFT() {

        Promotion promotion = new Promotion();
        promotion.setPromotionCondition(PromotionCondition.THIRD_PRODUCT_ON_GIFT);
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setIsActive(Boolean.TRUE);
        user.setUserPromotion(List.of(promotion));
        user.setTotalOrders(0);

        Product product = new Product();
        product.setId(productShort.getId());
        product.setName(productShort.getName());
        product.setPrice(BigDecimal.valueOf(120));

        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);
        promotion.setGiftProduct(product);

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setProduct(product);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));
        toCartItem.setUser(user);

        ShoppingCartItem toCartItem1 = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem1.setProduct(product);
        toCartItem1.setItemPrice(BigDecimal.valueOf(200));
        toCartItem1.setUser(user);

        shoppingCartItems = new ArrayList<>();
        shoppingCartItems.add(toCartItem);
        shoppingCartItems.add(toCartItem1);
        user.setShoppingCart(shoppingCartItems);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);
        when(cartItemRepo.save(any(ShoppingCartItem.class))).thenReturn(toCartItem);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo, times(2)).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }

    }

    @Test
    void addItemToShoppingCart_ifUserHavePromotionCondition_THIRD_PRODUCT_ON_GIFTAndFOR_CATEGORY() {

        Promotion promotion = new Promotion();
        promotion.setPromotionCondition(PromotionCondition.THIRD_PRODUCT_ON_GIFT);
        promotion.setPromotionType(PromotionType.FOR_CATEGORY);
        promotion.setIsActive(Boolean.TRUE);
        user.setUserPromotion(List.of(promotion));
        user.setTotalOrders(0);

        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(1L);
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);
        mainCategory.setSubcategories(List.of(subcategory));

        Product product = new Product();
        product.setId(productShort.getId());
        product.setName(productShort.getName());
        product.setPrice(BigDecimal.valueOf(120));
        product.setSubcategory(subcategory);
        product.setMainCategory(mainCategory);

        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);
        promotion.setGiftProduct(product);
        promotion.setForCategory(mainCategory);
        promotion.setSubcategory(subcategory);

        ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem.setProduct(product);
        toCartItem.setItemPrice(BigDecimal.valueOf(200));
        toCartItem.setUser(user);

        ShoppingCartItem toCartItem1 = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        toCartItem1.setProduct(product);
        toCartItem1.setItemPrice(BigDecimal.valueOf(200));
        toCartItem1.setUser(user);

        shoppingCartItems = new ArrayList<>();
        shoppingCartItems.add(toCartItem);
        shoppingCartItems.add(toCartItem1);
        user.setShoppingCart(shoppingCartItems);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
        when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
        when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);
        when(cartItemRepo.save(any(ShoppingCartItem.class))).thenReturn(toCartItem);

        List<ShoppingCartItemResponse> cartItemResponses =
                cartService.addItemToShoppingCart(user.getUsername(), itemRequest);

        verify(cartItemRepo, times(2)).save(any(ShoppingCartItem.class));
        for (ShoppingCartItemResponse itemResponse : cartItemResponses) {
            assertNotNull(itemResponse.getItemPrice());
        }
    }

        @Test
        void addItemToShoppingCart_ifUserHavePromotionCondition_IfHaveGiftProducts() {

            Promotion promotion = new Promotion();
            promotion.setPromotionCondition(PromotionCondition.THIRD_PRODUCT_ON_GIFT);
            promotion.setPromotionType(PromotionType.FOR_CATEGORY);
            promotion.setIsActive(Boolean.TRUE);
            user.setUserPromotion(List.of(promotion));
            user.setTotalOrders(0);

            MainCategory mainCategory = new MainCategory();
            mainCategory.setId(1L);
            Subcategory subcategory = new Subcategory();
            subcategory.setId(1L);
            mainCategory.setSubcategories(List.of(subcategory));

            Product product = new Product();
            product.setId(productShort.getId());
            product.setName(productShort.getName());
            product.setPrice(BigDecimal.valueOf(120));
            product.setSubcategory(subcategory);
            product.setMainCategory(mainCategory);

            promotion.setForProduct(product);
            promotion.setDiscountAmount(15);
            promotion.setGiftProduct(product);
            promotion.setForCategory(mainCategory);
            promotion.setSubcategory(subcategory);

            ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
            toCartItem.setProduct(product);
            toCartItem.setItemPrice(BigDecimal.valueOf(200));
            toCartItem.setUser(user);

            ShoppingCartItem toCartItem1 = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
            toCartItem1.setProduct(product);
            toCartItem1.setItemPrice(BigDecimal.valueOf(200));
            toCartItem1.setUser(user);
            toCartItem1.setGiftProduct(true);

            shoppingCartItems = new ArrayList<>();
            shoppingCartItems.add(toCartItem);
            shoppingCartItems.add(toCartItem1);
            user.setShoppingCart(shoppingCartItems);

            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
            when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
            when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);
            when(cartItemRepo.save(any(ShoppingCartItem.class))).thenReturn(toCartItem);

            List<ShoppingCartItemResponse> cartItemResponses =
                    cartService.addItemToShoppingCart(user.getUsername(), itemRequest);
            assertFalse(cartItemResponses.isEmpty());
        }

        @Test
        void addItemToShoppingCart_ifUserHavePromotionCondition_IfHaveGiftProduct() {

            Promotion promotion = new Promotion();
            promotion.setPromotionCondition(PromotionCondition.THIRD_PRODUCT_ON_GIFT);
            promotion.setPromotionType(PromotionType.FOR_CATEGORY);
            promotion.setIsActive(Boolean.TRUE);
            user.setUserPromotion(List.of(promotion));
            user.setTotalOrders(0);

            Product product = new Product();
            product.setId(productShort.getId());
            product.setName(productShort.getName());
            product.setPrice(BigDecimal.valueOf(120));

            promotion.setForProduct(product);
            promotion.setDiscountAmount(15);
            promotion.setGiftProduct(product);

            ShoppingCartItem toCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
            toCartItem.setProduct(product);
            toCartItem.setItemPrice(BigDecimal.valueOf(200));
            toCartItem.setUser(user);
            toCartItem.setGiftProduct(true);

            shoppingCartItems = new ArrayList<>();
            shoppingCartItems.add(toCartItem);
            user.setShoppingCart(shoppingCartItems);

            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            when(productService.getProductById(productShort.getId())).thenReturn(productResponse);
            when(productService.getProductById(additionalProduct.getId())).thenReturn(addProduct);
            when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);
            when(cartItemRepo.save(any(ShoppingCartItem.class))).thenReturn(toCartItem);

            List<ShoppingCartItemResponse> cartItemResponses =
                    cartService.addItemToShoppingCart(user.getUsername(), itemRequest);
            assertFalse(cartItemResponses.isEmpty());
        }

        @Test
        void deleteShoppingCartItem_ifItemIsPresent_SuccessDeleted () {
            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            ShoppingCartItem value = shoppingCartItems.get(0);
            Long id = value.getId();
            when(cartItemRepo.findByUserAndId(user, id)).thenReturn(Optional.of(value));
            when(cartItemRepo.existsById(id)).thenReturn(false);
            cartService.deleteShoppingCartItem(user.getUsername(), id);
            verify(cartItemRepo).deleteById(id);
        }

        @Test
        void deleteShoppingCartItem_ifItemIsPresent_ErrorDeleted () {
            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            ShoppingCartItem value = shoppingCartItems.get(0);
            Long id = value.getId();
            when(cartItemRepo.findByUserAndId(user, id)).thenReturn(Optional.of(value));
            when(cartItemRepo.existsById(id)).thenReturn(true);
            cartService.deleteShoppingCartItem(user.getUsername(), id);
            verify(cartItemRepo).deleteById(id);
        }

        @Test
        void deleteShoppingCartItem_ifItemIsEmpty () {
            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            ShoppingCartItem value = shoppingCartItems.get(0);
            Long id = value.getId();
            when(cartItemRepo.findByUserAndId(user, id)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () ->
                    cartService.deleteShoppingCartItem(user.getUsername(), id));

        }

        @Test
        void clearShoppingCart_ifShoppingCartIsNotEmpty () {
            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            when(cartItemRepo.findAllByUser(user)).thenReturn(shoppingCartItems);
            cartService.clearShoppingCart(user.getUsername());
            verify(cartItemRepo).deleteAll(shoppingCartItems);
        }

        @Test
        void clearShoppingCart_ifShoppingCartIsEmpty () {
            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            when(cartItemRepo.findAllByUser(user)).thenReturn(new ArrayList<>());
            assertThrows(EntityNotFoundException.class, () ->
                    cartService.clearShoppingCart(user.getUsername()));
        }

        @Test
        void changeCompositionItemCart_ifCartItemNotFound () {
            user.setShoppingCart(new ArrayList<>());
            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            when(cartItemRepo.findByUserAndId(user, 1L)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () ->
                    cartService.changeCompositionItemCart(user.getUsername(), 1L, itemRequest));
        }

        @Test
        void changeCompositionItemCart () {
            user.setShoppingCart(new ArrayList<>());
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.setId(1L);
            shoppingCartItem.setAdditionalIngredients(new ArrayList<>());
            shoppingCartItem.setExclusionIngredients(new ArrayList<>());
            itemRequest.setExclusionIngredients(new ArrayList<>());
            when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
            when(cartItemRepo.findByUserAndId(user, 1L)).thenReturn(Optional.of(shoppingCartItem));
            when(cartItemRepo.save(any(ShoppingCartItem.class))).thenReturn(shoppingCartItem);

            ShoppingCartItemResponse shoppingCartItemResponse =
                    cartService.changeCompositionItemCart(user.getUsername(), 1L, itemRequest);
            assertNotNull(shoppingCartItemResponse);
            assertFalse(shoppingCartItemResponse.getAdditionalIngredients().isEmpty());
            assertTrue(shoppingCartItemResponse.getExclusionIngredients().isEmpty());
        }
    }