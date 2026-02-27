package com.dineelite.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "booking_tables",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"table_id", "booking_date", "slot_id"})
       })
public class BookingTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_table_id")
    private Integer bookingTableId;


    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private TimeSlot slot;

    public Integer getBookingTableId() {
    return bookingTableId;
}

public void setBookingTableId(Integer bookingTableId) {
    this.bookingTableId = bookingTableId;
}


    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public RestaurantTable getTable() {
        return table;
    }

    public void setTable(RestaurantTable table) {
        this.table = table;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public void setSlot(TimeSlot slot) {
        this.slot = slot;
    }
}
