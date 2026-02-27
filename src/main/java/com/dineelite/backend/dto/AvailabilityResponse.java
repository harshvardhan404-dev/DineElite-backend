package com.dineelite.backend.dto;

public class AvailabilityResponse {

    private Integer tableId;
    private Integer capacity;
    private boolean isBooked;
    private boolean hasCapacity;

    public AvailabilityResponse(Integer tableId, Integer capacity, boolean isBooked, boolean hasCapacity) {
        this.tableId = tableId;
        this.capacity = capacity;
        this.isBooked = isBooked;
        this.hasCapacity = hasCapacity;
    }

    public Integer getTableId() {
        return tableId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public boolean hasCapacity() {
        return hasCapacity;
    }
}
