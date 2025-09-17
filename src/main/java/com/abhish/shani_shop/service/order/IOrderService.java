package com.abhish.shani_shop.service.order;

import java.util.List;

import com.abhish.shani_shop.dto.OrderDto;
import com.abhish.shani_shop.model.Order;
import com.abhish.shani_shop.model.User;

public interface IOrderService {
    Order placeOrder(User user);

    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(User user);
}
