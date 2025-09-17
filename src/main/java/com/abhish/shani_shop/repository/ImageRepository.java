package com.abhish.shani_shop.repository;

import java.util.List;

import com.abhish.shani_shop.model.Image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long productId);
}
