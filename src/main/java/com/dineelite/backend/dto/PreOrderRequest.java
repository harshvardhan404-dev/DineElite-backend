package com.dineelite.backend.dto;

import java.util.List;

public class PreOrderRequest {
    private Integer menuId;
    private Integer quantity;

    // Getters and Setters
    public Integer getMenuId() { return menuId; }
    public void setMenuId(Integer menuId) { this.menuId = menuId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
