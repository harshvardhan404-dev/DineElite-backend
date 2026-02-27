package com.dineelite.backend.dto;

public class HeatmapDataDTO {
    private Integer dayOfWeek; // 0 (Sun) to 6 (Sat)
    private String slotTime;
    private Long count;

    public HeatmapDataDTO(Integer dayOfWeek, String slotTime, Long count) {
        this.dayOfWeek = dayOfWeek;
        this.slotTime = slotTime;
        this.count = count;
    }

    // Getters and Setters
    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getSlotTime() { return slotTime; }
    public void setSlotTime(String slotTime) { this.slotTime = slotTime; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
