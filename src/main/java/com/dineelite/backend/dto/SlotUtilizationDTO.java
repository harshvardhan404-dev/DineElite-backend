package com.dineelite.backend.dto;

public class SlotUtilizationDTO {
    private String slotTime;
    private Double utilizationPercentage;

    public SlotUtilizationDTO(String slotTime, Double utilizationPercentage) {
        this.slotTime = slotTime;
        this.utilizationPercentage = utilizationPercentage;
    }

    // Getters and Setters
    public String getSlotTime() { return slotTime; }
    public void setSlotTime(String slotTime) { this.slotTime = slotTime; }
    public Double getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(Double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }
}
