package com.dineelite.backend.controller;

import com.dineelite.backend.service.BookingService;
import org.springframework.web.bind.annotation.*;
import com.dineelite.backend.dto.AvailabilityResponse;
import com.dineelite.backend.dto.BookingResponse;
import com.dineelite.backend.dto.CancelResponse;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/create")
    public BookingResponse createBooking(org.springframework.security.core.Authentication authentication,
                                @RequestParam Integer restaurantId,
                                @RequestParam String date,
                                @RequestParam Integer slotId,
                                @RequestParam Integer guestCount,
                                @RequestParam(required = false) Integer tableId) {
        
        // Get user from DB based on authentication
        String email = authentication.getName();
        com.dineelite.backend.entity.User user = bookingService.getUserByEmail(email);

        return bookingService.createBooking(
                user.getUserId(),
                restaurantId,
                LocalDate.parse(date),
                slotId,
                guestCount,
                tableId
        );
    }
    @GetMapping("/cancel/{bookingId}")
    public CancelResponse cancelBooking(org.springframework.security.core.Authentication authentication,
                                       @PathVariable Integer bookingId) {
        return bookingService.cancelBooking(bookingId, authentication.getName());
    }

    @GetMapping("/history")
    public List<com.dineelite.backend.dto.UserBookingResponse> getMyBookingHistory(org.springframework.security.core.Authentication authentication) {
        com.dineelite.backend.entity.User user = bookingService.getUserByEmail(authentication.getName());
        return bookingService.getUserBookingsDTO(user.getUserId());
    }

    @GetMapping("/admin/restaurant/{restaurantId}/count")
    public String getTotalBookings(@PathVariable Integer restaurantId) {
        return bookingService.getTotalBookingsForRestaurant(restaurantId);
    }

    @GetMapping("/admin/restaurant/{restaurantId}/peak-slot")
    public String getPeakSlot(@PathVariable Integer restaurantId) {
        return bookingService.getPeakSlotForRestaurant(restaurantId);
    }

    @GetMapping("/admin/restaurant/{restaurantId}/daily-trend")
    public String getDailyTrend(@PathVariable Integer restaurantId) {
        return bookingService.getDailyBookingTrend(restaurantId);
    }

    @GetMapping("/admin/restaurant/{restaurantId}/revenue")
    public String getTotalRevenue(@PathVariable Integer restaurantId) {
        return bookingService.getTotalRevenue(restaurantId);
    }

    @GetMapping("/admin/restaurant/{restaurantId}/analytics")
    public com.dineelite.backend.dto.DashboardAnalyticsDTO getDashboardAnalytics(@PathVariable Integer restaurantId) {
        return bookingService.getDashboardAnalytics(restaurantId);
    }

    @GetMapping("/availability")
    public List<AvailabilityResponse> checkAvailability(
            @RequestParam Integer restaurantId,
            @RequestParam String date,
            @RequestParam Integer slotId,
            @RequestParam Integer guestCount) {

        return bookingService.checkAvailability(
                restaurantId,
                LocalDate.parse(date),
                slotId,
                guestCount
        );
    }

    @GetMapping("/available-slots")
    public List<com.dineelite.backend.dto.TimeSlotResponse> getAvailableSlots(
            @RequestParam Integer restaurantId,
            @RequestParam String date,
            @RequestParam Integer guestCount) {
        
        return bookingService.getAvailableTimeSlots(
                restaurantId,
                LocalDate.parse(date),
                guestCount
        );
    }

    @GetMapping("/utilization")
    public String getUtilization(@RequestParam Integer restaurantId,
                                @RequestParam String date,
                                @RequestParam Integer slotId) {

        return bookingService.getTableUtilization(
                restaurantId,
                LocalDate.parse(date),
                slotId
        );
    }


}
