package com.abhish.shani_shop.request;

import java.math.BigDecimal;

import com.abhish.shani_shop.model.Category;

import lombok.Data;

@Data
public class ProductUpdateRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;

    private Category category;
}
