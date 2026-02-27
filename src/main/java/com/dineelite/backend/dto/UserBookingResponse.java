package com.dineelite.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class UserBookingResponse {
    private Integer bookingId;
    private String restaurantName;
    private String address;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer guestCount;
    private String status;
    private Double depositAmount;
    private String paymentStatus;
    private String dietaryNotes;

    public UserBookingResponse(Integer bookingId, String restaurantName, String address, LocalDate bookingDate,
                               LocalTime startTime, LocalTime endTime, Integer guestCount, String status,
                               Double depositAmount, String paymentStatus, String dietaryNotes) {
        this.bookingId = bookingId;
        this.restaurantName = restaurantName;
        this.address = address;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.guestCount = guestCount;
        this.status = status;
        this.depositAmount = depositAmount;
        this.paymentStatus = paymentStatus;
        this.dietaryNotes = dietaryNotes;
    }

    public String getDietaryNotes() { return dietaryNotes; }

    // Getters and Setters
    public Integer getBookingId() { return bookingId; }
    public String getRestaurantName() { return restaurantName; }
    public String getAddress() { return address; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public Integer getGuestCount() { return guestCount; }
    public String getStatus() { return status; }
    public Double getDepositAmount() { return depositAmount; }
    public String getPaymentStatus() { return paymentStatus; }
}
