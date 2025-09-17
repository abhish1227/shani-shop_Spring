package com.abhish.shani_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDownload {
    private byte[] content;
    private String fileName;
    private String fileType;
}
