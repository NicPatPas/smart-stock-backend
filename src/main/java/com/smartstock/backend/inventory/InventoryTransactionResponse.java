package com.smartstock.backend.inventory;

import java.time.LocalDateTime;

public class InventoryTransactionResponse {

    private Long id;
    private Long productId;
    private InventoryTransactionType type;
    private Integer quantity;
    private LocalDateTime timestamp;
    private String changedBy;
    private String note;

    public InventoryTransactionResponse() {
    }

    public InventoryTransactionResponse(Long id,
                                        Long productId,
                                        InventoryTransactionType type,
                                        Integer quantity,
                                        LocalDateTime timestamp,
                                        String changedBy,
                                        String note) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.changedBy = changedBy;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public InventoryTransactionType getType() {
        return type;
    }

    public void setType(InventoryTransactionType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
