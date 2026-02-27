package com.dineelite.backend.dto;

public class BookingResponse {

    private String message;
    private Integer bookingId;
    private Integer tableId;
    private String status;

    public BookingResponse(String message,
                           Integer bookingId,
                           Integer tableId,
                           String status) {
        this.message = message;
        this.bookingId = bookingId;
        this.tableId = tableId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public String getStatus() {
        return status;
    }
}
