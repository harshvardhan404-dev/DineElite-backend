package com.dineelite.backend.dto;

import java.util.List;

public class DashboardAnalyticsDTO {
    private Long totalBookings;
    private String peakSlot;
    private Double totalRevenue;
    private Double averageUtilization;
    private List<DailyTrendDTO> dailyTrends;
    private List<SlotUtilizationDTO> slotUtilizations;
    private List<HeatmapDataDTO> bookingHeatmap;

    public DashboardAnalyticsDTO() {}

    // Getters and Setters
    public Long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(Long totalBookings) { this.totalBookings = totalBookings; }
    public String getPeakSlot() { return peakSlot; }
    public void setPeakSlot(String peakSlot) { this.peakSlot = peakSlot; }
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    public Double getAverageUtilization() { return averageUtilization; }
    public void setAverageUtilization(Double averageUtilization) { this.averageUtilization = averageUtilization; }
    public List<DailyTrendDTO> getDailyTrends() { return dailyTrends; }
    public void setDailyTrends(List<DailyTrendDTO> dailyTrends) { this.dailyTrends = dailyTrends; }
    public List<SlotUtilizationDTO> getSlotUtilizations() { return slotUtilizations; }
    public void setSlotUtilizations(List<SlotUtilizationDTO> slotUtilizations) { this.slotUtilizations = slotUtilizations; }
    public List<HeatmapDataDTO> getBookingHeatmap() { return bookingHeatmap; }
    public void setBookingHeatmap(List<HeatmapDataDTO> bookingHeatmap) { this.bookingHeatmap = bookingHeatmap; }
}
