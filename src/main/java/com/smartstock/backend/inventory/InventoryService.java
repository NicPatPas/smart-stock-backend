package com.smartstock.backend.inventory;

import com.smartstock.backend.product.Product;
import com.smartstock.backend.product.ProductNotFoundException;
import com.smartstock.backend.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryService(ProductRepository productRepository,
                            InventoryTransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    public InventoryTransaction addStock(Long productId, int quantity, String changedBy, String note) {
        validatePositiveQuantity(quantity);
        Product product = loadProduct(productId);
        product.setStock(product.getStock() + quantity);
        product.setUpdatedAt(LocalDateTime.now());

        InventoryTransaction transaction = createTransaction(product, InventoryTransactionType.ADD, quantity, changedBy, note);
        productRepository.save(product);
        return transactionRepository.save(transaction);
    }

    public InventoryTransaction removeStock(Long productId, int quantity, String changedBy, String note) {
        validatePositiveQuantity(quantity);
        Product product = loadProduct(productId);
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock");
        }

        product.setStock(product.getStock() - quantity);
        product.setUpdatedAt(LocalDateTime.now());

        InventoryTransaction transaction = createTransaction(product, InventoryTransactionType.REMOVE, quantity, changedBy, note);
        productRepository.save(product);
        return transactionRepository.save(transaction);
    }

    public InventoryTransaction correctStock(Long productId, int adjustmentQuantity, String changedBy, String note) {
        if (adjustmentQuantity == 0) {
            throw new IllegalArgumentException("Correction quantity must not be zero");
        }

        Product product = loadProduct(productId);
        int newStock = product.getStock() + adjustmentQuantity;
        if (newStock < 0) {
            throw new InsufficientStockException("Insufficient stock");
        }

        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        InventoryTransaction transaction = createTransaction(product, InventoryTransactionType.CORRECTION, adjustmentQuantity, changedBy, note);
        productRepository.save(product);
        return transactionRepository.save(transaction);
    }

    public List<InventoryTransaction> getHistory(Long productId) {
        Product product = loadProduct(productId);
        return transactionRepository.findByProductOrderByTimestampDesc(product);
    }

    private Product loadProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    private InventoryTransaction createTransaction(Product product,
                                                   InventoryTransactionType type,
                                                   int quantity,
                                                   String changedBy,
                                                   String note) {
        return new InventoryTransaction(product, type, quantity, LocalDateTime.now(), changedBy, note);
    }

    private void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}
