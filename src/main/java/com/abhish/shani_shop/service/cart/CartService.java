package com.abhish.shani_shop.service.cart;

import java.math.BigDecimal;
import java.util.List;

import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.Cart;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.repository.CartItemRepository;
import com.abhish.shani_shop.repository.CartRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Cart getCart(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

    }

    @Transactional
    @Override
    public void clearCart(Cart cart) {

        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.delete(cart);
        return;
    }

    @Override
    public BigDecimal getTotalPrice(Cart cart) {

        return cart.getTotalAmount();
    }

    @Transactional
    @Override
    public Cart initializeNewCart(User user) {

        if (user.getCart() == null) {
            Cart newCart = new Cart();
            newCart.setTotalAmount(BigDecimal.ZERO);
            newCart.setUser(user);
            return cartRepository.save(newCart);
        }

        return getCartByUserId(user.getId());

    }

    @Override
    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUser_Id(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("No cart exists for the given user.");
        }
        return cart;
    }
}
