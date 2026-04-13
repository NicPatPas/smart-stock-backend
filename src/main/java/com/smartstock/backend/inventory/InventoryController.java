package com.smartstock.backend.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PatchMapping("/admin/inventory/{productId}/add")
    public ResponseEntity<InventoryTransactionResponse> addStock(
            @PathVariable Long productId,
            @RequestBody InventoryChangeRequest request) {
        InventoryTransaction transaction = inventoryService.addStock(productId, request.getQuantity(), request.getChangedBy(), request.getNote());
        return ResponseEntity.ok(mapResponse(transaction));
    }

    @PatchMapping("/admin/inventory/{productId}/remove")
    public ResponseEntity<InventoryTransactionResponse> removeStock(
            @PathVariable Long productId,
            @RequestBody InventoryChangeRequest request) {
        InventoryTransaction transaction = inventoryService.removeStock(productId, request.getQuantity(), request.getChangedBy(), request.getNote());
        return ResponseEntity.ok(mapResponse(transaction));
    }

    @PatchMapping("/admin/inventory/{productId}/correct")
    public ResponseEntity<InventoryTransactionResponse> correctStock(
            @PathVariable Long productId,
            @RequestBody InventoryChangeRequest request) {
        InventoryTransaction transaction = inventoryService.correctStock(productId, request.getQuantity(), request.getChangedBy(), request.getNote());
        return ResponseEntity.ok(mapResponse(transaction));
    }

    @GetMapping("/inventory/history/{productId}")
    public ResponseEntity<List<InventoryTransactionResponse>> getHistory(@PathVariable Long productId) {
        List<InventoryTransactionResponse> history = inventoryService.getHistory(productId)
                .stream()
                .map(this::mapResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }

    private InventoryTransactionResponse mapResponse(InventoryTransaction transaction) {
        return new InventoryTransactionResponse(
                transaction.getId(),
                transaction.getProduct().getId(),
                transaction.getType(),
                transaction.getQuantity(),
                transaction.getTimestamp(),
                transaction.getChangedBy(),
                transaction.getNote()
        );
    }
}
