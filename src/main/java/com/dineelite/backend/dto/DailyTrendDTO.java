package com.dineelite.backend.dto;

import java.time.LocalDate;

public class DailyTrendDTO {
    private LocalDate date;
    private Long count;

    public DailyTrendDTO(LocalDate date, Long count) {
        this.date = date;
        this.count = count;
    }

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
