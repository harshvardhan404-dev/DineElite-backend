package com.dineelite.backend.dto;

public class MenuItemResponse {
    private Integer menuId;
    private String itemName;
    private Double price;
    private Boolean isAvailable;

    public MenuItemResponse(Integer menuId, String itemName, Double price, Boolean isAvailable) {
        this.menuId = menuId;
        this.itemName = itemName;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public Integer getMenuId() { return menuId; }
    public String getItemName() { return itemName; }
    public Double getPrice() { return price; }
    public Boolean getIsAvailable() { return isAvailable; }
}
