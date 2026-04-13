package com.smartstock.backend.product;

public class ProductRequest {

    private String name;
    private String sku;
    private String description;
    private Double price;
    private Integer stock;
    private Integer lowStockThreshold;
    private Long categoryId;

    public ProductRequest() {
    }

    public ProductRequest(String name, String sku, String description, Double price, Integer stock, Integer lowStockThreshold, Long categoryId) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.lowStockThreshold = lowStockThreshold;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
