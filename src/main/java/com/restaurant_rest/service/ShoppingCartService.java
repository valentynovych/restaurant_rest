package com.restaurant_rest.service;

import com.restaurant_rest.entity.*;
import com.restaurant_rest.entity.enums.OrderStatus;
import com.restaurant_rest.entity.enums.PromotionType;
import com.restaurant_rest.exception.ForbiddenUpdateException;
import com.restaurant_rest.mapper.OrderMapper;
import com.restaurant_rest.mapper.ShoppingCartMapper;
import com.restaurant_rest.model.order.OrderResponse;
import com.restaurant_rest.model.product.ProductResponse;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemRequest;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemResponse;
import com.restaurant_rest.repositoty.OrderRepo;
import com.restaurant_rest.repositoty.ShoppingCartItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ShoppingCartService {

    private final UserService userService;
    private final ProductService productService;
    private final ShoppingCartItemRepo shoppingCartItemRepo;
    private final OrderRepo orderRepo;

    public List<ShoppingCartItemResponse> getUserCartItems(String username) {
        log.info("getUserCartItems() -> start, with username: " + username);
        User userByEmail = userService.getUserByEmail(username);
        List<ShoppingCartItem> shoppingCart = userByEmail.getShoppingCart();
        List<ShoppingCartItemResponse> cartItemResponses =
                ShoppingCartMapper.MAPPER.cartItemListToCartItemResponseList(shoppingCart);
        log.info("getUserCartItems() -> exit, return all ShoppingCartItem, list size: " + cartItemResponses.size());
        return cartItemResponses;
    }

    public List<ShoppingCartItemResponse> addItemToShoppingCart(String username, ShoppingCartItemRequest itemRequest) {
        log.info("getUserCartItems() -> start, with username: " + username);
        User userByEmail = userService.getUserByEmail(username);
        ShoppingCartItem shoppingCartItem = ShoppingCartMapper.MAPPER.requestCartItemToCartItem(itemRequest);
        shoppingCartItem = calculateCartItemPrice(shoppingCartItem);
        shoppingCartItem.setUser(userByEmail);
        log.info("getUserCartItems() -> saved new cart item");
        shoppingCartItemRepo.save(shoppingCartItem);
        List<ShoppingCartItem> shoppingCartItems =
                applyPromotionToShoppingCart(userByEmail.getShoppingCart(), userByEmail.getUserPromotion());
        List<ShoppingCartItemResponse> cartItemResponses =
                ShoppingCartMapper.MAPPER.cartItemListToCartItemResponseList(shoppingCartItems);
        log.info("getUserCartItems() -> exit, return shopping cart, size: " + cartItemResponses.size());
        return cartItemResponses;
    }

    public boolean deleteShoppingCartItem(String username, Long id) {
        log.info("deleteShoppingCartItem() -> start, with username: " + username +
                " cart id: " + id);
        User userByEmail = userService.getUserByEmail(username);
        List<ShoppingCartItem> shoppingCart = userByEmail.getShoppingCart()
                .stream()
                .filter(shoppingCartItem -> shoppingCartItem.getId().equals(id))
                .toList();
        if (shoppingCart.isEmpty()) {
            log.error(String.format("deleteShoppingCartItem() -> " +
                    "ShoppingCartItem by id: %s not owned user: %s", id, username));
            throw new ForbiddenUpdateException(
                    String.format("Елемент корзини з id: %s не належить користувачу - %s", id, username));
        }
        shoppingCartItemRepo.deleteById(id);
        if (!shoppingCartItemRepo.existsById(id)) {
            log.info("deleteShoppingCartItem() -> success deleting ShoppingCartItem by id: " + id);
            return true;
        }
        log.error("deleteShoppingCartItem() -> any error on deleting ShoppingCartItem by id: " + id);
        return false;
    }

    public void clearShoppingCart(String username) {
        log.info("clearShoppingCart() -> start, with username: " + username);
        User userByEmail = userService.getUserByEmail(username);
        List<ShoppingCartItem> shoppingCart = userByEmail.getShoppingCart();
        if (shoppingCart.isEmpty()) {
            throw new EmptyResultDataAccessException(String.format(
                    "Корзина користувача %s була пуста", username), 1);
        }
        shoppingCartItemRepo.deleteAll(shoppingCart);
        log.info("clearShoppingCart() -> success clear shopping cart, with username: " + username);
    }

    public ShoppingCartItemResponse changeCompositionItemCart(String username,
                                                              Long id,
                                                              ShoppingCartItemRequest itemRequest) {
        log.info(String.format("changeCompositionItemCart() -> start, with username: %s, cart item id: %s", username, id));
        User userByEmail = userService.getUserByEmail(username);
        List<ShoppingCartItem> shoppingCart = userByEmail.getShoppingCart()
                .stream()
                .filter(shoppingCartItem -> shoppingCartItem.getId().equals(id))
                .toList();

        if (shoppingCart.isEmpty()) {
            log.error("changeCompositionItemCart() -> shopping cart is not exist cart item with id: " + id);
            throw new ForbiddenUpdateException(
                    String.format("Елемент корзини з id: %s не належить користувачу - %s", id, username));
        } else {
            ShoppingCartItem shoppingCartItem = shoppingCart.stream().findFirst().get();
            shoppingCartItem.setAdditionalIngredients(
                    itemRequest.getAdditionalIngredients()
                            .stream()
                            .map(ShoppingCartMapper.MAPPER::productShortToProduct)
                            .toList());

            shoppingCartItem.setExclusionIngredients(
                    itemRequest.getExclusionIngredients()
                            .stream()
                            .map(ShoppingCartMapper.MAPPER::productShortToProduct)
                            .toList());
            log.info(String.format("changeCompositionItemCart() -> save edit cart item with id: %s", id));
            ShoppingCartItem save = shoppingCartItemRepo.save(shoppingCartItem);
            ShoppingCartItemResponse itemResponse = ShoppingCartMapper.MAPPER.cartItemToCartItemResponse(save);
            log.info("changeCompositionItemCart() -> exit, return cart item");
            return itemResponse;
        }
    }



    private List<ShoppingCartItem> applyPromotionToShoppingCart(List<ShoppingCartItem> shoppingCart,
                                                                List<Promotion> promotions) {
        log.info("applyPromotionToShoppingCart() -> start, shopping cart size: " + shoppingCart.size());
        if (promotions == null || promotions.isEmpty()) {
            log.info("applyPromotionToShoppingCart() -> exit, promotion list is null or empty");
            return shoppingCart;
        }

        for (Promotion promotion : promotions) {
            if (promotion.getIsActive()) {
                log.info("applyPromotionToShoppingCart() -> apply promotion with id: " + promotion.getId());
                shoppingCart = applyPromotion(shoppingCart, promotion);
            }
        }
        log.info("applyPromotionToShoppingCart() -> exit, return shopping cart size: " + shoppingCart.size());
        return shoppingCart;
    }

    private ShoppingCartItem calculateCartItemPrice(ShoppingCartItem cartItem) {
        log.info("calculateCartItemPrice() -> start, with cart item id: " + cartItem.getId());
        ProductResponse byId = productService.getProductById(cartItem.getProduct().getId());
        cartItem.setItemPrice(byId.getPrice());
        if (cartItem.getAdditionalIngredients() != null
                && !cartItem.getAdditionalIngredients().isEmpty()) {
            for (Product product : cartItem.getAdditionalIngredients()) {
                log.info("calculateCartItemPrice() -> cartItem has additional product, id :" + product.getId());
                BigDecimal itemPrice = cartItem.getItemPrice();
                itemPrice = itemPrice.add(product.getPrice());
                cartItem.setItemPrice(itemPrice);
            }
        }
        log.info("calculateCartItemPrice() -> exit. return cart item, his price: " + cartItem.getItemPrice());
        return cartItem;
    }

    private List<ShoppingCartItem> applyPromotion(List<ShoppingCartItem> shoppingCart, Promotion promotion) {
        log.info(String.format("applyPromotion() -> start, " +
                "shopping cart size: %s, promotion id: %s", shoppingCart.size(), promotion.getId()));
        Set<ShoppingCartItem> shoppingCartItems = new HashSet<>(shoppingCart);
        List<ShoppingCartItem> itemArrayList = new ArrayList<>(shoppingCart);

        for (int i = 0; i < itemArrayList.size(); i++) {
            ShoppingCartItem item = itemArrayList.get(i);
            if (item.isGiftProduct()) {
                log.info(String.format("applyPromotion() -> item with id: %s isGiftProduct", item.getId()));
                break;
            } else {
                List<ShoppingCartItem> giftsProduct = shoppingCartItems.stream().filter(ShoppingCartItem::isGiftProduct).toList();
                if (!giftsProduct.isEmpty() &&
                        giftsProduct.stream().anyMatch(cartItem ->
                                cartItem.getProduct().getId().equals(promotion.getGiftProduct().getId()))) {
                    log.info(String.format("applyPromotion() -> shopping cart exist product id: %s isGiftProduct", item.getId()));
                    break;
                }
            }

            switch (promotion.getPromotionCondition()) {
                case PERCENT_FOR_CATEGORY -> {
                    log.info("applyPromotion() -> PromotionCondition is PERCENT_FOR_CATEGORY");
                    var product = item.getProduct();
                    if (product.getMainCategory().getId().equals(promotion.getForCategory().getId())
                            && product.getSubcategory().getId().equals(promotion.getSubcategory().getId())) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                    item.setItemSalePrice(
                            calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                }
                case PERCENT_FOR_PRODUCT -> {
                    log.info("applyPromotion() -> PromotionCondition is PERCENT_FOR_PRODUCT");
                    if (item.getProduct().getId().equals(promotion.getForProduct().getId())) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case PERCENT_OF_AMOUNT -> {
                    log.info("applyPromotion() -> PromotionCondition is PERCENT_OF_AMOUNT");
                    BigDecimal totalAmount = calculateTotalAmountShoppingCart(shoppingCart);
                    if (totalAmount.compareTo(BigDecimal.valueOf(promotion.getMinimalAmount())) > 0) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case PERCENT_ON_BIRTHDAY -> {
                    log.info("applyPromotion() -> PromotionCondition is PERCENT_ON_BIRTHDAY");
                    var orderedUser = item.getUser();
                    var today = new Date();
                    if (orderedUser.getUserDetails().getDateOfBirth().equals(today)) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case FIRST_BUY -> {
                    log.info("applyPromotion() -> PromotionCondition is FIRST_BUY");
                    var orderedUser = item.getUser();
                    if (orderedUser.getTotalOrders() == 0) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case THIRD_PRODUCT_ON_GIFT -> {
                    log.info("applyPromotion() -> PromotionCondition is THIRD_PRODUCT_ON_GIFT");
                    var promotionType = promotion.getPromotionType();
                    if (promotionType.equals(PromotionType.FOR_PRODUCT)) {
                        log.info("applyPromotion() -> PromotionCondition is THIRD_PRODUCT_ON_GIFT and PromotionType.FOR_PRODUCT");
                        List<ShoppingCartItem> filtered = itemArrayList.stream().filter(orderItem ->
                                orderItem.getProduct().getId().equals(promotion.getForProduct().getId())).toList();
                        if (!filtered.isEmpty() && filtered.size() >= 2) {
                            shoppingCart.add(shoppingCartItemRepo.save(
                                    createGiftOrderItem(promotion.getGiftProduct(), item.getUser())));
                            filtered.forEach(itemArrayList::remove);
                        }
                    } else if (promotionType.equals(PromotionType.FOR_CATEGORY)) {
                        log.info("applyPromotion() -> PromotionCondition is THIRD_PRODUCT_ON_GIFT and PromotionType.FOR_CATEGORY");
                        List<ShoppingCartItem> filtered = itemArrayList.stream().filter(orderItem ->
                                orderItem.getProduct().getMainCategory().getId().equals(promotion.getForCategory().getId())
                                        && orderItem.getProduct().getSubcategory().getId().equals(
                                        promotion.getSubcategory().getId())).toList();
                        if (!filtered.isEmpty() && filtered.size() >= 2) {
                            shoppingCart.add(shoppingCartItemRepo.save(
                                    createGiftOrderItem(promotion.getGiftProduct(), item.getUser())));
                            filtered.forEach(itemArrayList::remove);
                        }
                    }
                }
            }
        }
        log.info("applyPromotion() -> exit, return shopping cart size: " + shoppingCart.size());
        return shoppingCart;
    }

    private BigDecimal calculateSalePriceOnPercent(BigDecimal price, Integer discount) {
        log.info(String.format("calculateSalePriceOnPercent() -> start, with price: %s, discount: %s ", price, discount));
        return price.subtract(price.multiply(new BigDecimal(discount).divide(new BigDecimal(100))));
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

    private ShoppingCartItem createGiftOrderItem(Product giftProduct, User user) {
        log.info("createGiftOrderItem() -> start, with username: " + user.getUsername());
        ShoppingCartItem cartItem = new ShoppingCartItem();
        cartItem.setGiftProduct(true);
        cartItem.setItemSalePrice(new BigDecimal("0.1"));
        cartItem.setItemPrice(giftProduct.getPrice());
        cartItem.setProduct(giftProduct);
        cartItem.setUser(user);
        log.info("createGiftOrderItem() -> exit, return new ShoppingCartItem");
        return cartItem;
    }


}
