package com.dineelite.backend.dto;

public class CancelResponse {

    private String message;
    private Integer bookingId;
    private String status;

    public CancelResponse(String message,
                          Integer bookingId,
                          String status) {
        this.message = message;
        this.bookingId = bookingId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public String getStatus() {
        return status;
    }
}
