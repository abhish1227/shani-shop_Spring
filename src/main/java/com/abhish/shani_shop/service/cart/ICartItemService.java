package com.abhish.shani_shop.service.cart;

import com.abhish.shani_shop.dto.CartItemDto;
import com.abhish.shani_shop.model.Cart;
import com.abhish.shani_shop.model.CartItem;

public interface ICartItemService {
    CartItemDto addItemToCart(Long cartId, Long productId, int quantity);

    void removeItemFromCart(Cart cart, Long itemId);

    void updateItemQuantity(Cart cart, Long productId, int quantity);

    CartItem getCartItem(Cart cart, Long productId);
}
