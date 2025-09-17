package com.abhish.shani_shop.Controller;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.abhish.shani_shop.dto.CartItemDto;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.Cart;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.response.APIResponse;
import com.abhish.shani_shop.service.cart.ICartItemService;
import com.abhish.shani_shop.service.cart.ICartService;
import com.abhish.shani_shop.service.user.IUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;

    @PostMapping("/item/add")
    public ResponseEntity<APIResponse> addItemToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        try {

            User user = userService.getAuthenticatedUser();

            Cart cart = cartService.initializeNewCart(user);

            CartItemDto cartItemDto = cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok(new APIResponse("Item added to cart successfully!", cartItemDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new APIResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/item/remove")
    public ResponseEntity<APIResponse> removeItemFromCart(@RequestParam Long productId) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = user.getCart();
            if (cart == null) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No cart exists for the logged in user.", null));
            }
            cartItemService.removeItemFromCart(cart, productId);
            return ResponseEntity.ok(new APIResponse("Item removed from cart successfully!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/item/update")
    public ResponseEntity<APIResponse> updateItemQuantity(@RequestParam Long productId,
            @RequestParam Integer quantity) {
        try {
            if (quantity <= 0)
                return ResponseEntity.status(NOT_ACCEPTABLE)
                        .body(new APIResponse("Please enter a valid quantity.", null));
            User user = userService.getAuthenticatedUser();
            Cart cart = user.getCart();
            if (cart == null) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No cart exists for the logged in user.", null));
            }
            cartItemService.updateItemQuantity(user.getCart(), productId, quantity);
            return ResponseEntity.ok(new APIResponse("Item quantity updated successfully!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }
}
