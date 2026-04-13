package com.smartstock.backend.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

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
            @RequestBody InventoryChangeRequest request,
            Principal principal) {
        InventoryTransaction transaction = inventoryService.addStock(productId, request.getQuantity(), principal.getName(), request.getNote());
        return ResponseEntity.ok(mapResponse(transaction));
    }

    @PatchMapping("/admin/inventory/{productId}/remove")
    public ResponseEntity<InventoryTransactionResponse> removeStock(
            @PathVariable Long productId,
            @RequestBody InventoryChangeRequest request,
            Principal principal) {
        InventoryTransaction transaction = inventoryService.removeStock(productId, request.getQuantity(), principal.getName(), request.getNote());
        return ResponseEntity.ok(mapResponse(transaction));
    }

    @PatchMapping("/admin/inventory/{productId}/correct")
    public ResponseEntity<InventoryTransactionResponse> correctStock(
            @PathVariable Long productId,
            @RequestBody InventoryChangeRequest request,
            Principal principal) {
        InventoryTransaction transaction = inventoryService.correctStock(productId, request.getQuantity(), principal.getName(), request.getNote());
        return ResponseEntity.ok(mapResponse(transaction));
    }

    @GetMapping("/inventory/history/{productId}")
    public ResponseEntity<List<InventoryTransactionResponse>> getHistory(@PathVariable Long productId) {
        List<InventoryTransactionResponse> history = inventoryService.getHistory(productId)
                .stream()
                .map(this::mapResponse)
                .toList();
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
