package com.abhish.shani_shop.service.product;

import java.util.List;
import java.util.Optional;

import com.abhish.shani_shop.dto.CategoryDto;
import com.abhish.shani_shop.dto.ImageDto;
import com.abhish.shani_shop.dto.ProductDto;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.Category;
import com.abhish.shani_shop.model.Product;
import com.abhish.shani_shop.repository.CategoryRepository;
import com.abhish.shani_shop.repository.ProductRepository;
import com.abhish.shani_shop.request.AddProductRequest;
import com.abhish.shani_shop.request.ProductUpdateRequest;
import com.abhish.shani_shop.service.image.ImageFetchService;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * The class ProductService implements the interface IProductService.
 */
@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageFetchService imageFetchService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Product addProduct(AddProductRequest request) {
        // check if the category is found in the DB
        // If yes, set it as a new product's category
        // If not, then save it as a new category and set it as a new product's category
        if (request.getCategory() == null || request.getCategory().getName() == null) {
            throw new IllegalArgumentException("Category name must not be null");
        }
        String categoryName = request.getCategory().getName().trim();

        Category category = Optional.ofNullable(categoryRepository.findByName(categoryName))
                .orElseGet(() -> {
                    Category newCategory = new Category(categoryName);
                    try {
                        return categoryRepository.save(newCategory);
                    } catch (DataIntegrityViolationException e) {
                        // Handle race condition: re-fetch the category
                        Category existing = categoryRepository.findByName(categoryName);
                        if (existing == null) {
                            throw new IllegalStateException("Failed to resolve category after save conflict");
                        }
                        return existing;
                    }
                });

        Product product = createProduct(request, category);
        return productRepository.save(product);

    }

    private Product createProduct(AddProductRequest request, Category category) {
        // return new Product(
        // request.getName(),
        // request.getBrand(),
        // request.getPrice(),
        // request.getInventory(),
        // request.getDescription(),
        // category);
        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        // Set other fields as needed
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete, () -> {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        });
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryNameIgnoreCase(category.trim());
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrandIgnoreCase(brand.trim());
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrandAllIgnoreCase(category.trim(), brand.trim());
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameIgnoreCase(name.trim());
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndNameAllIgnoreCase(brand.trim(), name.trim());
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndNameAllIgnoreCase(brand.trim(), name.trim());
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {

        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request)).map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        // Category category =
        // categoryRepository.findByName(request.getCategory().getName());
        // existingProduct.setCategory(category);
        if (request.getCategory() != null && request.getCategory().getName() != null) {
            String categoryName = request.getCategory().getName().trim();

            Category category = Optional.ofNullable(categoryRepository.findByName(categoryName))
                    .orElseGet(() -> {
                        Category newCategory = new Category(categoryName);
                        try {
                            return categoryRepository.save(newCategory);
                        } catch (DataIntegrityViolationException e) {
                            Category existing = categoryRepository.findByName(categoryName);
                            if (existing == null) {
                                throw new IllegalStateException("Failed to resolve category after save conflict");
                            }
                            return existing;
                        }
                    });

            existingProduct.setCategory(category);
        }
        return existingProduct;
    }

    @Override
    public List<ProductDto> convertToDtoList(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<ImageDto> imageDtos = imageFetchService.getImageDtosForProduct(product.getId());
        productDto.setImages(imageDtos);

        if (product.getCategory() != null) {
            productDto.setCategory(modelMapper.map(product.getCategory(), CategoryDto.class));
        } else {
            productDto.setCategory(null);
        }
        return productDto;
    }

}
