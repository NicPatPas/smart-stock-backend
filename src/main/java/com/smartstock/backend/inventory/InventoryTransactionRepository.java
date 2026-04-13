package com.smartstock.backend.inventory;

import com.smartstock.backend.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProductOrderByTimestampDesc(Product product);
}
