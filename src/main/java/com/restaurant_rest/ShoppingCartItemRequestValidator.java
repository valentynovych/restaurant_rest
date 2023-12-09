package com.restaurant_rest;

import com.restaurant_rest.entity.Product;
import com.restaurant_rest.model.product.ProductShort;
import com.restaurant_rest.model.shopping_cart.ShoppingCartItemRequest;
import com.restaurant_rest.repositoty.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ShoppingCartItemRequestValidator implements Validator {
    private final ProductRepo productRepo;

    @Override
    public boolean supports(Class<?> clazz) {
        return ShoppingCartItemRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ShoppingCartItemRequest request = (ShoppingCartItemRequest) target;

        if (request.getProduct().getId() == null || request.getProduct().getId() <= 0) {
            errors.rejectValue("product",
                    "product.error",
                    String.format("Не допустиме значення id: %s", request.getProduct().getId()));
        } else {
            Long id = request.getProduct().getId();
            Optional<Product> byId = productRepo.findById(id);
            if (byId.isEmpty()) {
                errors.rejectValue("product",
                        "product.error",
                        String.format("Продукт з id: %s - не знайдено", id));
            } else {
                Product product = byId.get();
                if (product.getIsIngredient()) {
                    errors.rejectValue("product",
                            "product.error",
                            String.format("Продукт з id: %s не може бути основним", id));
                }
            }
        }

        if (!request.getAdditionalIngredients().isEmpty()) {
            List<ProductShort> additionalIngredients = request.getAdditionalIngredients();
            for (int i = 0; i < additionalIngredients.size(); i++) {
                ProductShort productShort = additionalIngredients.get(i);
                validateProductShort(productShort, errors, "additionalIngredients", i);
            }
        }

        if (!request.getExclusionIngredients().isEmpty()) {
            List<ProductShort> exclusionIngredients = request.getExclusionIngredients();
            for (int i = 0; i < exclusionIngredients.size(); i++) {
                ProductShort productShort = exclusionIngredients.get(i);
                validateProductShort(productShort, errors, "exclusionIngredients", i);
            }
        }
    }

    private void validateProductShort(ProductShort productShort, Errors errors, String forGroup, int iter) {
        if (productShort.getId() == null || productShort.getId() <= 0) {
            errors.rejectValue(String.format("%s[%s].id", forGroup, iter),
                    String.format("error.%s[%s].id", forGroup, iter),
                    String.format("Не допустиме значення id: %s", productShort.getId()));
        } else {
            Optional<Product> byId1 = productRepo.findById(productShort.getId());
            if (byId1.isEmpty()) {
                errors.rejectValue(String.format("%s[%s].id", forGroup, iter),
                        String.format("error.%s[%s].id", forGroup, iter),
                        String.format("Продукт з id: %s не знайденно", productShort.getId()));
            } else {
                Product product = byId1.get();
                if (!product.getIsIngredient()) {
                    errors.rejectValue(String.format("%s[%s].id", forGroup, iter),
                            String.format("error.%s[%s].id", forGroup, iter),
                            String.format("Продукт з id: %s не є інгредієнтом", productShort.getId()));
                }
            }
        }
    }
}
