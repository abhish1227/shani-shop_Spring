package com.abhish.shani_shop.Controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import com.abhish.shani_shop.dto.ImageDownload;
import com.abhish.shani_shop.dto.ImageDto;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
// import com.abhish.shani_shop.model.Image;
import com.abhish.shani_shop.response.APIResponse;
import com.abhish.shani_shop.service.image.IImageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
// import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse> saveImages(@RequestParam List<MultipartFile> files,
            @RequestParam Long productId) {

        try {
            List<ImageDto> imageDtos = imageService.saveImages(files, productId);
            return ResponseEntity.ok(new APIResponse("Images uploaded successfully", imageDtos));
        } catch (Exception e) {
            logger.error("Failed to upload images", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to upload images: " + e.getMessage(), null));
        }
    }

    @GetMapping("/image/download/{imageId}")
    public ResponseEntity<?> downloadImage(@PathVariable Long imageId) {
        try {
            ImageDownload download = imageService.getImageDownload(imageId);
            ByteArrayResource resource = new ByteArrayResource(download.getContent());

            String contentType = download.getFileType() != null
                    ? download.getFileType()
                    : MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + download.getFileName() + "\"")
                    .contentLength(download.getContent().length)
                    .body(resource);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new APIResponse("Image not found with ID: " + imageId, null));
        } catch (Exception e) {
            logger.error("Failed to download image {}", imageId, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to download image: " + e.getMessage(), null));
        }
    }

    @PutMapping(value = "/image/update/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse> updateImage(@PathVariable Long imageId,
            @RequestParam("file") MultipartFile file) {
        try {
            imageService.updateImage(file, imageId);
            return ResponseEntity.ok(new APIResponse("Image updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Failed to update image {}", imageId, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to update image: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/image/delete/{imageId}")
    public ResponseEntity<APIResponse> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImageById(imageId);
            return ResponseEntity.ok(new APIResponse("Image deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Failed to delete image {}", imageId, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to delete image: " + e.getMessage(), null));
        }
    }

}
