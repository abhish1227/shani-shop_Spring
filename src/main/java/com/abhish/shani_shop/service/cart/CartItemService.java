package com.abhish.shani_shop.service.cart;

import java.math.BigDecimal;

import com.abhish.shani_shop.dto.CartItemDto;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.Cart;
import com.abhish.shani_shop.model.CartItem;
import com.abhish.shani_shop.model.Product;
import com.abhish.shani_shop.repository.CartItemRepository;
import com.abhish.shani_shop.repository.CartRepository;
import com.abhish.shani_shop.service.product.IProductService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public CartItemDto addItemToCart(Long cartId, Long productId, int quantity) {

        // get the cart and product
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);

        if (product.getInventory() < quantity) {
            throw new IllegalStateException(
                    "Not enough items in the inventory. Please try adding a quantity lesser than or equal to "
                            + product.getInventory() + ".");
        }

        // check if the product is already in the cart
        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(new CartItem());

        // if product is not in the cart, create a new cart item
        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        }
        // else update the quantity
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        // set the total price
        cartItem.setTotalPrice();
        // add the cart item to the cart
        cart.addItem(cartItem);
        // save the cart item and cart
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

        CartItemDto cartItemDto = modelMapper.map(cartItem, CartItemDto.class);

        return cartItemDto;
    }

    @Transactional
    @Override
    public void removeItemFromCart(Cart cart, Long productId) {

        CartItem cartItem = getCartItem(cart, productId);

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        return;
    }

    @Transactional
    @Override
    public void updateItemQuantity(Cart cart, Long productId, int quantity) {
        cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> {
                    if (item.getProduct().getInventory() < quantity) {
                        throw new IllegalStateException(
                                "Not enough items in the inventory.Please try updating to a quantity lesser than or equal to "
                                        + item.getProduct().getInventory() + ".");
                    }
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                }, () -> {
                    throw new ResourceNotFoundException("Cart item not found");
                });

        BigDecimal totalAmount = cart.getItems().stream().map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);

    }

    @Override
    public CartItem getCartItem(Cart cart, Long productId) {

        return cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
    }

}
