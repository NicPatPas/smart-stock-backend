package com.smartstock.backend.inventory;

import com.smartstock.backend.product.Product;
import com.smartstock.backend.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Keyboard");
        product.setSku("KEY-001");
        product.setStock(10);
        product.setLowStockThreshold(5);
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setCreatedAt(LocalDateTime.now().minusDays(1));
        product.setUpdatedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    void addStockIncreasesProductStockAndCreatesTransaction() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(InventoryTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryTransaction result = inventoryService.addStock(1L, 5, "tester", "incoming delivery");

        assertThat(result.getType()).isEqualTo(InventoryTransactionType.ADD);
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getChangedBy()).isEqualTo("tester");
        assertThat(result.getProduct().getStock()).isEqualTo(15);

        verify(productRepository).save(product);
        verify(transactionRepository).save(result);
    }

    @Test
    void removeStockThrowsWhenInsufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> inventoryService.removeStock(1L, 20, "tester", "sell order"))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Insufficient stock");
    }

    @Test
    void correctStockAppliesNegativeAdjustment() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(InventoryTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryTransaction result = inventoryService.correctStock(1L, -3, "auditor", "inventory count");

        assertThat(result.getType()).isEqualTo(InventoryTransactionType.CORRECTION);
        assertThat(result.getQuantity()).isEqualTo(-3);
        assertThat(result.getProduct().getStock()).isEqualTo(7);
    }

    @Test
    void correctStockThrowsWhenQuantityZero() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> inventoryService.correctStock(1L, 0, "tester", "bad request"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Correction quantity must not be zero");
    }

    @Test
    void getHistoryReturnsTransactionList() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        InventoryTransaction transaction = new InventoryTransaction(product, InventoryTransactionType.ADD, 5, LocalDateTime.now(), "tester", "note");
        when(transactionRepository.findByProductOrderByTimestampDesc(product)).thenReturn(List.of(transaction));

        List<InventoryTransaction> history = inventoryService.getHistory(1L);

        assertThat(history).containsExactly(transaction);
    }
}
