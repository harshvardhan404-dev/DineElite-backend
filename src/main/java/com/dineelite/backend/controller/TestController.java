package com.dineelite.backend.controller;

import com.dineelite.backend.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
    private final BookingTableRepository bookingTableRepository;

    public TestController(UserRepository userRepository,
                          RestaurantRepository restaurantRepository,
                          RestaurantTableRepository tableRepository,
                          TimeSlotRepository timeSlotRepository,
                          BookingRepository bookingRepository,
                          BookingTableRepository bookingTableRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.bookingRepository = bookingRepository;
        this.bookingTableRepository = bookingTableRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "Users: " + userRepository.count()
                + ", Restaurants: " + restaurantRepository.count()
                + ", Tables: " + tableRepository.count()
                + ", Slots: " + timeSlotRepository.count()
                + ", Bookings: " + bookingRepository.count()
                + ", BookingTables: " + bookingTableRepository.count();
    }
}

