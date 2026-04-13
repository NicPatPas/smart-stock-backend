package com.smartstock.backend.product;

import com.smartstock.backend.category.Category;
import com.smartstock.backend.category.CategoryRepository;
import com.smartstock.backend.category.CategoryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        validateRequest(request, false);
        Category category = loadCategory(request.getCategoryId());

        Product product = new Product(
                request.getName().trim(),
                request.getSku().trim(),
                request.getDescription(),
                request.getPrice() != null ? BigDecimal.valueOf(request.getPrice()) : null,
                request.getStock(),
                request.getLowStockThreshold(),
                category,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        productRepository.save(product);
        return toResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        validateRequest(request, true);
        Product product = loadProduct(id);
        Category category = loadCategory(request.getCategoryId());

        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU must be unique");
        }

        product.setName(request.getName().trim());
        product.setSku(request.getSku().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice() != null ? BigDecimal.valueOf(request.getPrice()) : null);
        product.setStock(request.getStock());
        product.setLowStockThreshold(request.getLowStockThreshold());
        product.setCategory(category);
        product.setUpdatedAt(LocalDateTime.now());

        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = loadProduct(id);
        productRepository.delete(product);
    }

    public ProductResponse getProductById(Long id) {
        return toResponse(loadProduct(id));
    }

    public List<ProductResponse> searchProducts(String name,
                                                String sku,
                                                Long categoryId,
                                                Boolean lowStock,
                                                Boolean available) {
        return productRepository.findAll().stream()
                .filter(product -> name == null || product.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(product -> sku == null || product.getSku().toLowerCase().contains(sku.toLowerCase()))
                .filter(product -> categoryId == null || product.getCategory() != null && product.getCategory().getId().equals(categoryId))
                .filter(product -> lowStock == null || lowStock.equals(product.getStock() <= product.getLowStockThreshold()))
                .filter(product -> available == null || available.equals(product.getStock() > 0))
                .map(this::toResponse)
                .toList();
    }

    private Product loadProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    private Category loadCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID must be provided");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

    private void validateRequest(ProductRequest request, boolean existingProduct) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (request.getSku() == null || request.getSku().isBlank()) {
            throw new IllegalArgumentException("SKU must not be blank");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("Stock must be zero or positive");
        }
        if (request.getLowStockThreshold() == null || request.getLowStockThreshold() < 0) {
            throw new IllegalArgumentException("Low stock threshold must be zero or positive");
        }
        if (request.getPrice() != null && request.getPrice() < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        if (!existingProduct && productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU must be unique");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must be provided");
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getLowStockThreshold(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
