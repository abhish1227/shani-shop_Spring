package com.abhish.shani_shop.Controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import com.abhish.shani_shop.dto.OrderDto;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.Order;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.response.APIResponse;
import com.abhish.shani_shop.service.order.IOrderService;
import com.abhish.shani_shop.service.user.IUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;
    private final IUserService userService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse> createOrder() {
        try {
            User user = userService.getAuthenticatedUser();
            Order order = orderService.placeOrder(user);
            return ResponseEntity.ok(new APIResponse("Order placed successfully", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Error occurred!", e.getMessage()));
        }
    }

    @GetMapping("/get/orderById/{orderId}")
    public ResponseEntity<APIResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrder(orderId);
            return ResponseEntity.ok(new APIResponse("Order fetched successfully!", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Error occurred!", e.getMessage()));
        }
    }

    @GetMapping("/get/userOrders")
    public ResponseEntity<APIResponse> getUserOrders() {
        try {
            User user = userService.getAuthenticatedUser();
            List<OrderDto> orders = orderService.getUserOrders(user);
            return ResponseEntity.ok(new APIResponse("Order fetched successfully!", orders));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Error occurred!", e.getMessage()));
        }
    }
}
