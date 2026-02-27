package com.dineelite.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.dineelite.backend.enums.BookingStatus;
import com.dineelite.backend.enums.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private TimeSlot slot;

    @Column(nullable = false)
    private Integer guestCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
private BookingStatus status;


    private Double depositAmount;

    @Enumerated(EnumType.STRING)
private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String guestDietaryNotes;

    @Version
    private Integer version;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
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

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGuestDietaryNotes() {
        return guestDietaryNotes;
    }

    public void setGuestDietaryNotes(String guestDietaryNotes) {
        this.guestDietaryNotes = guestDietaryNotes;
    }
}
