package com.abhish.shani_shop.service.image;

import java.util.List;

import com.abhish.shani_shop.dto.ImageDto;
import com.abhish.shani_shop.model.Image;
import com.abhish.shani_shop.repository.ImageRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageFetchService {
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public List<ImageDto> getImageDtosForProduct(Long productId) {
        List<Image> images = imageRepository.findByProductId(productId);
        return images.stream().map(img -> {
            ImageDto dto = new ImageDto();
            dto.setImageId(img.getId());
            dto.setImageName(img.getFileName());
            dto.setDownloadUrl(img.getDownloadUrl());
            return dto;
        }).toList();
    }
}
