package com.abhish.shani_shop.repository;

import java.util.List;

import com.abhish.shani_shop.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Id(Long userId);
}
