package com.abhish.shani_shop.Controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import com.abhish.shani_shop.dto.ProductDto;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.Product;
import com.abhish.shani_shop.request.AddProductRequest;
import com.abhish.shani_shop.request.ProductUpdateRequest;
import com.abhish.shani_shop.response.APIResponse;
import com.abhish.shani_shop.service.product.IProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping("/public/all")
    public ResponseEntity<APIResponse> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDto> convertedProducts = productService.convertToDtoList(products);
            return ResponseEntity.ok(new APIResponse("Products fetched successfully", convertedProducts));
        } catch (Exception e) {
            logger.error("Failed to fetch products", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to fetch products", null));
        }
    }

    @GetMapping("/public/product/{id}")
    public ResponseEntity<APIResponse> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductDto convertedProduct = productService.convertToDto(product);
            return ResponseEntity.ok(new APIResponse("Product fetched successfully", convertedProduct));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/seller/add")
    public ResponseEntity<APIResponse> addProduct(@Valid @RequestBody AddProductRequest product) {
        try {
            Product newProduct = productService.addProduct(product);
            return ResponseEntity.ok(new APIResponse("Product added successfully", newProduct));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to add product." + " " + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR hasRole('SELLER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateProduct(@Valid @RequestBody ProductUpdateRequest product,
            @PathVariable Long id) {
        try {
            Product updatedProduct = productService.updateProduct(product, id);
            return ResponseEntity.ok(new APIResponse("Product updated successfully", updatedProduct));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to update product." + " " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok(new APIResponse("Product deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to delete product." + " " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/by/name/{name}")
    public ResponseEntity<APIResponse> getProductsByName(@PathVariable String name) {
        try {
            List<Product> products = productService.getProductsByName(name);

            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No products found with name: " + name, null));
            }
            List<ProductDto> convertedProducts = productService.convertToDtoList(products);
            return ResponseEntity.ok(new APIResponse("Products fetched successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to fetch products." + " " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/by/brand/{brand}")
    public ResponseEntity<APIResponse> getProductsByBrand(@PathVariable String brand) {
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No products found with brand: " + brand, null));
            }
            List<ProductDto> convertedProducts = productService.convertToDtoList(products);
            return ResponseEntity.ok(new APIResponse("Products fetched successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to fetch products." + " " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/by/brand-and-name")
    public ResponseEntity<APIResponse> getProductsByBrandAndName(@RequestParam String brand,
            @RequestParam String name) {

        try {
            List<Product> products = productService.getProductsByBrandAndName(brand, name);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No products found for brand: " + brand + " and name: " + name, null));
            }
            List<ProductDto> convertedProducts = productService.convertToDtoList(products);
            return ResponseEntity.ok(new APIResponse("Products fetched successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to fetch products." + " " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/by/category/{category}")
    public ResponseEntity<APIResponse> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No products found with category: " + category, null));
            }
            List<ProductDto> convertedProducts = productService.convertToDtoList(products);
            return ResponseEntity.ok(new APIResponse("Products fetched successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to fetch products." + " " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/by/category-and-brand")
    public ResponseEntity<APIResponse> getProductsByCategoryAndBrand(@RequestParam String category,
            @RequestParam String brand) {

        try {
            List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND)
                        .body(new APIResponse("No products found for category: " + category + " and brand: " + brand,
                                null));
            }
            List<ProductDto> convertedProducts = productService.convertToDtoList(products);
            return ResponseEntity.ok(new APIResponse("Products fetched successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Failed to fetch products." + " " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/count/brand-and-name")
    public ResponseEntity<APIResponse> countProductsByBrandAndName(@RequestParam String brand,
            @RequestParam String name) {
        try {
            Long count = productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new APIResponse("Product count fetched successfully", count));
        } catch (Exception e) {
            return ResponseEntity.ok(new APIResponse("Failed to fetch product count." + " " + e.getMessage(), null));
        }
    }

}
