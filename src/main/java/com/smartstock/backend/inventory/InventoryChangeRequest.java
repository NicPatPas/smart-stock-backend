package com.smartstock.backend.inventory;

public class InventoryChangeRequest {

    private Integer quantity;
    private String changedBy;
    private String note;

    public InventoryChangeRequest() {
    }

    public InventoryChangeRequest(Integer quantity, String changedBy, String note) {
        this.quantity = quantity;
        this.changedBy = changedBy;
        this.note = note;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
