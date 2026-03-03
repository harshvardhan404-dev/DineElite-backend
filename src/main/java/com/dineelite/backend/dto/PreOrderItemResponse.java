package com.dineelite.backend.dto;

public class PreOrderItemResponse {
    private String itemName;
    private Integer quantity;
    private Double price;

    public PreOrderItemResponse(String itemName, Integer quantity, Double price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getItemName() { return itemName; }
    public Integer getQuantity() { return quantity; }
    public Double getPrice() { return price; }
}
