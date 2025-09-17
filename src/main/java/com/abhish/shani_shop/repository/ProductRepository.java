package com.abhish.shani_shop.repository;

import java.util.List;

import com.abhish.shani_shop.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findById(long id);

    List<Product> findByCategoryNameIgnoreCase(String category);

    List<Product> findByBrandIgnoreCase(String category);

    List<Product> findByCategoryNameAndBrandAllIgnoreCase(String category, String brand);

    List<Product> findByNameIgnoreCase(String name);

    List<Product> findByBrandAndNameAllIgnoreCase(String brand, String name);

    Long countByBrandAndNameAllIgnoreCase(String brand, String name);
}
