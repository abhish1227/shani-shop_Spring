package com.abhish.shani_shop.service.image;

import java.util.List;

import com.abhish.shani_shop.dto.ImageDownload;
import com.abhish.shani_shop.dto.ImageDto;
import com.abhish.shani_shop.model.Image;

import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    Image getImageById(Long id);

    void deleteImageById(Long id);

    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);

    void updateImage(MultipartFile file, Long imageId);

    ImageDownload getImageDownload(Long imageId);
}
