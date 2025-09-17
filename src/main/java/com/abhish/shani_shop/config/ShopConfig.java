package com.abhish.shani_shop.config;

import com.abhish.shani_shop.dto.ProductDto;
import com.abhish.shani_shop.model.Product;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShopConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Skip mapping of images to avoid LOB loading and recursion
        mapper.typeMap(Product.class, ProductDto.class)
                .addMappings(m -> m.skip(ProductDto::setImages));

        return mapper;
    }
}
