package com.dineelite.backend.dto;

import java.util.List;

public class BookingPostRequest {
    private Integer restaurantId;
    private String date;
    private Integer slotId;
    private Integer guestCount;
    private Integer tableId;
    private List<PreOrderRequest> preOrders;

    // Getters and Setters
    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Integer getSlotId() { return slotId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public Integer getTableId() { return tableId; }
    public void setTableId(Integer tableId) { this.tableId = tableId; }
    public List<PreOrderRequest> getPreOrders() { return preOrders; }
    public void setPreOrders(List<PreOrderRequest> preOrders) { this.preOrders = preOrders; }
}
