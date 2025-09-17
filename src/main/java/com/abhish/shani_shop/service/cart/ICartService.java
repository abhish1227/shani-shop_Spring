package com.abhish.shani_shop.service.cart;

import java.math.BigDecimal;
import java.util.List;

import com.abhish.shani_shop.model.Cart;
import com.abhish.shani_shop.model.User;

public interface ICartService {
    Cart getCart(Long id);

    void clearCart(Cart cart);

    BigDecimal getTotalPrice(Cart cart);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);

    List<Cart> getAllCarts();
}
