package com.abhish.shani_shop.service.product;

import java.util.List;

import com.abhish.shani_shop.dto.ProductDto;
import com.abhish.shani_shop.model.Product;
import com.abhish.shani_shop.request.AddProductRequest;
import com.abhish.shani_shop.request.ProductUpdateRequest;

public interface IProductService {
    Product addProduct(AddProductRequest request);

    Product getProductById(Long id);

    void deleteProductById(Long id);

    Product updateProduct(ProductUpdateRequest request, Long productId);

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByBrand(String brand);

    List<Product> getProductsByCategoryAndBrand(String category, String brand);

    List<Product> getProductsByName(String name);

    List<Product> getProductsByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);

    ProductDto convertToDto(Product product);

    List<ProductDto> convertToDtoList(List<Product> products);

}
