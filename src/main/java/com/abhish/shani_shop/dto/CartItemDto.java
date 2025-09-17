package com.abhish.shani_shop.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemDto {
    private Long itemId;

    private int quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}
