package com.abhish.shani_shop.request;

import java.math.BigDecimal;

import com.abhish.shani_shop.model.Category;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductRequest {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String brand;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @Min(0)
    private int inventory;
    private String description;

    @NotNull
    private Category category;
}
