package com.abhish.shani_shop.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import com.abhish.shani_shop.dto.OrderDto;
import com.abhish.shani_shop.enums.OrderStatus;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.Cart;
import com.abhish.shani_shop.model.CartItem;
import com.abhish.shani_shop.model.Order;
import com.abhish.shani_shop.model.OrderItem;
import com.abhish.shani_shop.model.Product;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.repository.OrderRepository;
import com.abhish.shani_shop.repository.ProductRepository;
import com.abhish.shani_shop.service.cart.ICartService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Order placeOrder(User user) {

        Cart cart = cartService.getCartByUserId(user.getId());

        Order order = createOrder(cart);

        for (CartItem cartItem : cart.getItems()) {
            int available = cartItem.getProduct().getInventory();
            int demand = cartItem.getQuantity();
            if (available == 0)
                throw new IllegalArgumentException("Item with id " + cartItem.getId() + " out of stock!");

            if (demand > available) {
                throw new IllegalArgumentException("Item with id " + cartItem.getId() + " has only " + available
                        + " units available in the inventory.");
            }
        }

        List<OrderItem> orderItemList = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(cart);

        return savedOrder;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.setInventory(product.getInventory() - cartItem.getQuantity());
            productRepository.save(product);
            return new OrderItem(
                    order, product, cartItem.getQuantity(), cartItem.getUnitPrice());
        }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));
    }

    @Override
    public List<OrderDto> getUserOrders(User user) {
        List<Order> orders = orderRepository.findByUser_Id(user.getId());
        return orders.stream().map(this::convertToDto).toList();

    }

    private OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

}
