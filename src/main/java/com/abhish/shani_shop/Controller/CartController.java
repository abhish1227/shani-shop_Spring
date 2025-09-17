package com.abhish.shani_shop.Controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;

import com.abhish.shani_shop.dto.CartDto;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.response.APIResponse;
import com.abhish.shani_shop.service.cart.ICartService;
import com.abhish.shani_shop.service.user.IUserService;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final ICartService cartService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/cart")
    public ResponseEntity<APIResponse> getCart() {
        try {
            User user = userService.getAuthenticatedUser();
            if (user.getCart() == null) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No cart exists for the logged in user.", null));
            }
            return ResponseEntity
                    .ok(new APIResponse("Cart fetched successfully!", modelMapper.map(user.getCart(), CartDto.class)));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<APIResponse> clearCart() {
        try {
            User user = userService.getAuthenticatedUser();
            if (user.getCart() == null) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No cart exists for the logged in user.", null));
            }
            cartService.clearCart(user.getCart());
            return ResponseEntity.ok(new APIResponse("Cart cleared successfully!", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/total")
    public ResponseEntity<APIResponse> getTotalAmount() {
        try {
            User user = userService.getAuthenticatedUser();
            if (user.getCart() == null) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No cart exists for the logged in user.", null));
            }
            BigDecimal totalAmount = cartService.getTotalPrice(user.getCart());
            return ResponseEntity.ok(new APIResponse("Total amount fetched successfully!", totalAmount));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }
}
