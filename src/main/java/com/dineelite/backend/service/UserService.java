package com.dineelite.backend.service;

import com.dineelite.backend.entity.*;
import com.dineelite.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingTableRepository bookingTableRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Delete Social interactions (Likes, Comments) associated with the user
        likeRepository.deleteByUser(user);
        commentRepository.deleteByUser(user);

        // 2. Delete Notifications where user is recipient or sender
        notificationRepository.deleteByRecipient(user);
        notificationRepository.deleteBySender(user);

        // 3. Handle data if user is an ADMIN (Restaurant Owner)
        if ("ADMIN".equals(user.getRole())) {
            Optional<Restaurant> restaurantOpt = restaurantRepository.findByAdminUserId(userId);
            if (restaurantOpt.isPresent()) {
                Restaurant restaurant = restaurantOpt.get();

                // Delete all restaurant dependent data
                // Advertisements (and their likes/comments)
                List<Advertisement> ads = advertisementRepository.findByRestaurant(restaurant);
                for (Advertisement ad : ads) {
                    likeRepository.deleteByAdvertisement(ad);
                    commentRepository.deleteByAdvertisement(ad);
                }
                advertisementRepository.deleteAllInBatch(ads);

                // Bookings (and their table links)
                List<Booking> bookings = bookingRepository.findByRestaurant(restaurant);
                for (Booking booking : bookings) {
                    bookingTableRepository.deleteByBooking(booking);
                }
                bookingRepository.deleteAllInBatch(bookings);

                // Reviews
                reviewRepository.deleteByRestaurant_RestaurantId(restaurant.getRestaurantId());

                // Menu, and Tables
                menuItemRepository.deleteByRestaurant(restaurant);
                restaurantTableRepository.deleteByRestaurant(restaurant);

                // Finally delete the restaurant
                restaurantRepository.delete(restaurant);
            }
        } 
        
        // 4. Handle data if user is a CUSTOMER
        // They might have bookings at other restaurants
        List<Booking> userBookings = bookingRepository.findByUser(user);
        for (Booking booking : userBookings) {
            bookingTableRepository.deleteByBooking(booking);
        }
        bookingRepository.deleteAllInBatch(userBookings);

        // Delete reviews written by the user
        reviewRepository.deleteByUser_UserId(userId);

        // 5. Finally delete the user
        userRepository.delete(user);
    }
}
