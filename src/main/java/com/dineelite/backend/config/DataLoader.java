package com.dineelite.backend.config;

import com.dineelite.backend.entity.*;
import com.dineelite.backend.enums.BookingStatus;
import com.dineelite.backend.enums.MediaType;
import com.dineelite.backend.enums.PaymentStatus;
import com.dineelite.backend.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository,
            TimeSlotRepository timeSlotRepository,
            RestaurantTableRepository tableRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository,
            BookingTableRepository bookingTableRepository,
            AdvertisementRepository advertisementRepository,
            LikeRepository likeRepository,
            CommentRepository commentRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println(">>> Starting DineElite Data Injection...");

            // Ensure new columns exist
            try {
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS is_enabled BOOLEAN DEFAULT TRUE");
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_token VARCHAR(255)");
            } catch (Exception e) {
                System.out.println(">>> Note: Column check skipped or failed: " + e.getMessage());
            }

            // 1. Create users using raw SQL (bypasses JPA transaction issues with Supabase pooler)
            try {
                String hashedAdmin = passwordEncoder.encode("admin");
                jdbcTemplate.execute("INSERT INTO users (full_name, email, password, role, is_enabled) " +
                    "VALUES ('General Admin', 'admin@dineelite.com', '" + hashedAdmin + "', 'ADMIN', true) " +
                    "ON CONFLICT (email) DO NOTHING");
                
                for (int i = 1; i <= 10; i++) {
                    String hashedPass = passwordEncoder.encode("password" + i);
                    jdbcTemplate.execute("INSERT INTO users (full_name, email, password, role, is_enabled) " +
                        "VALUES ('Restaurant Admin " + i + "', 'admin" + i + "@dineelite.com', '" + hashedPass + "', 'ADMIN', true) " +
                        "ON CONFLICT (email) DO NOTHING");
                }

                String hashedCustomer = passwordEncoder.encode("pass");
                jdbcTemplate.execute("INSERT INTO users (full_name, email, password, role, is_enabled) " +
                    "VALUES ('Rahul Sharma', 'rahul@test.com', '" + hashedCustomer + "', 'CUSTOMER', true) " +
                    "ON CONFLICT (email) DO NOTHING");
                System.out.println(">>> All base users ensured.");
            } catch (Exception e) {
                System.out.println(">>> ERROR creating users: " + e.getMessage());
            }

            // 2. Seed restaurants and their dependencies (Menu, Tables, Slots)
            try {
                long existingCount = restaurantRepository.count();
                System.out.println(">>> Checking data seeding... (existing restaurants: " + existingCount + ")");
                { // Always run cleanup/seeding in this phase to fix corrupt data
                    System.out.println(">>> Starting full data verification and cleanup...");
                    String[][] restaurantData = {
                        {"Fine Dine Palace", "Pune", "09:00", "22:00", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4", "500", "Luxury dining with the finest global cuisine.", "Global", "18.5204", "73.8567"},
                        {"The Spice Garden", "Mumbai", "11:00", "23:00", "https://images.unsplash.com/photo-1552566626-52f8b828add9", "300", "Authentic Indian spices in a lush garden setting.", "Indian", "19.0760", "72.8777"},
                        {"Coastal Cravings", "Goa", "12:00", "23:00", "https://images.unsplash.com/photo-1559339352-11d035aa65de", "450", "Fresh seafood and tropical vibes by the beach.", "Seafood", "15.2993", "74.1240"},
                        {"Zen Sushi Hub", "Bangalore", "10:00", "22:00", "https://images.unsplash.com/photo-1579027989536-b7b1f875659b", "600", "Minimalist Japanese dining with expert sushi chefs.", "Japanese", "12.9716", "77.5946"},
                        {"Little Italy", "Delhi", "11:30", "22:30", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5", "400", "Classic Italian pasta and wood-fired pizzas.", "Italian", "28.6139", "77.2090"},
                        {"Steak House Prime", "Hyderabad", "17:00", "23:00", "https://images.unsplash.com/photo-1544025162-d76694265947", "700", "Premium cuts and aged steaks for the meat lover.", "Steakhouse", "17.3850", "78.4867"},
                        {"The Breakfast Club", "Chennai", "07:00", "15:00", "https://images.unsplash.com/photo-1482049016688-2d3e1b311543", "200", "All-day breakfast and artisanal coffee.", "Continental", "13.0827", "80.2707"},
                        {"Dragon Bowl", "Kolkata", "12:00", "23:00", "https://images.unsplash.com/photo-1525755662778-989d0524087e", "350", "Spicy Szechuan and traditional Cantonese treats.", "Chinese", "22.5726", "88.3639"},
                        {"Mediterranean Mist", "Pune", "11:00", "22:00", "https://images.unsplash.com/photo-1544124499-5223d6973844", "400", "Healthy salads and grilled meats from the Levant.", "Mediterranean", "18.5204", "73.8567"},
                        {"Vibrant Veggie", "Surat", "10:00", "22:00", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd", "150", "Pure vegetarian delights with farm-fresh produce.", "Vegetarian", "21.1702", "72.8311"}
                    };

                    for (int i = 0; i < restaurantData.length; i++) {
                        try {
                            String[] data = restaurantData[i];
                            
                            // Find existing or create new
                            Restaurant r = restaurantRepository.findByName(data[0]).orElse(null);
                            if (r == null) {
                                r = new Restaurant();
                                r.setName(data[0]);
                                // ... will be saved below ...
                            }

                            User admin = userRepository.findByEmail("admin" + (i + 1) + "@dineelite.com").orElse(null);
                            if (admin == null) {
                                System.out.println(">>> Skipping restaurant " + data[0] + " - admin not found");
                                continue;
                            }

                            r.setAddress(data[1]);
                            r.setOpeningTime(LocalTime.parse(data[2]));
                            r.setClosingTime(LocalTime.parse(data[3]));
                            r.setImageUrl(data[4]);
                            r.setDepositAmount(Double.parseDouble(data[5]));
                            r.setDescription(data[6]);
                            r.setCuisine(data[7]);
                            r.setLatitude(Double.parseDouble(data[8]));
                            r.setLongitude(Double.parseDouble(data[9]));
                            r.setHouseRules("Standard House Rules: Dress code smart casual.");
                            r.setAdmin(admin);
                            r = restaurantRepository.save(r);

                            // Clear existing dependencies FAST using raw SQL if they are cluttered
                            System.out.println(">>> Performing fast cleanup for " + r.getName() + " (ID: " + r.getRestaurantId() + ")...");
                            jdbcTemplate.execute("DELETE FROM time_slots WHERE restaurant_id = " + r.getRestaurantId());
                            jdbcTemplate.execute("DELETE FROM restaurant_tables WHERE restaurant_id = " + r.getRestaurantId());
                            jdbcTemplate.execute("DELETE FROM menu_items WHERE restaurant_id = " + r.getRestaurantId());
                            jdbcTemplate.execute("DELETE FROM advertisements WHERE restaurant_id = " + r.getRestaurantId());

                            System.out.println(">>> Re-seeding components for " + r.getName() + "...");
                            addMenu(r, menuItemRepository);
                            addTables(r, tableRepository);
                            addSlots(r, timeSlotRepository);
                            System.out.println(">>> Finished restaurant: " + data[0]);
                        } catch (Exception e) {
                            System.out.println(">>> Error seeding restaurant " + restaurantData[i][0] + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    System.out.println(">>> Restaurant seeding finished. Total: " + restaurantRepository.count());
                }
            } catch (Exception e) {
                System.out.println(">>> Restaurant seeding err: " + e.getMessage());
                e.printStackTrace();
            }

            // 3. Seed Advertisements (Elite Moments)
            try {
                if (advertisementRepository.count() == 0) {
                    System.out.println(">>> Seeding advertisements...");
                    List<Restaurant> restList = restaurantRepository.findAll();
                    String[] captions = {
                        "Experience the finest dining in town! ✨ #FineDining #Elite",
                        "Our Signature Dish is finally back! 🍷 #MustTry #Gourmet",
                        "The perfect ambiance for your special moments. ❤️ #RomanticDinner",
                        "Fresh ingredients, expert chefs, unforgettable taste. 👨‍🍳 #ChefLife",
                        "Limited time special menu available now! 🍣 #Foodie #DineElite"
                    };
                    String[] images = {
                        "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b",
                        "https://images.unsplash.com/photo-1504674900247-0877df9cc836",
                        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0",
                        "https://images.unsplash.com/photo-1559339352-11d035aa65de",
                        "https://images.unsplash.com/photo-1552566626-52f8b828add9"
                    };

                    for (Restaurant r : restList) {
                        for (int i = 0; i < 2; i++) {
                            Advertisement ad = new Advertisement();
                            ad.setRestaurant(r);
                            ad.setMediaUrl(images[new Random().nextInt(images.length)]);
                            ad.setMediaType(MediaType.POST);
                            ad.setCaption(captions[new Random().nextInt(captions.length)]);
                            advertisementRepository.save(ad);
                        }
                    }
                    System.out.println(">>> Advertisements seeded.");
                }
            } catch (Exception e) {
                System.out.println(">>> Ad seeding err: " + e.getMessage());
            }

            // 4. Seed Bookings (Dashboard Data)
            try {
                if (bookingRepository.count() == 0) {
                    System.out.println(">>> Seeding bookings...");
                    User customer = userRepository.findByEmail("rahul@test.com").orElse(null);
                    List<Restaurant> restList = restaurantRepository.findAll();
                    if (customer != null && !restList.isEmpty()) {
                        for (Restaurant r : restList) {
                            List<TimeSlot> slots = timeSlotRepository.findByRestaurant(r);
                            if (slots.isEmpty()) continue;

                            for (int d = -7; d <= 0; d++) { // Past 7 days
                                for (int i = 0; i < 2; i++) { // 2 bookings per day
                                    Booking b = new Booking();
                                    b.setRestaurant(r);
                                    b.setUser(customer);
                                    b.setBookingDate(LocalDate.now().plusDays(d));
                                    b.setSlot(slots.get(new Random().nextInt(slots.size())));
                                    b.setGuestCount(2 + new Random().nextInt(4));
                                    b.setStatus(BookingStatus.COMPLETED);
                                    b.setPaymentStatus(PaymentStatus.PAID);
                                    b.setDepositAmount(r.getDepositAmount());
                                    b.setCreatedAt(LocalDateTime.now().plusDays(d));
                                    bookingRepository.save(b);
                                }
                            }
                        }
                        System.out.println(">>> Bookings seeded.");
                    }
                }
            } catch (Exception e) {
                System.out.println(">>> Booking seeding err: " + e.getMessage());
            }

            System.out.println(">>> DineElite Data Injection COMPLETE.");
        };
    }

    private void addMenu(Restaurant r, MenuItemRepository repo) {
        String[] items = {"Signature Dish", "Chef's Special", "Classic Favorite", "Desert Delight"};
        double[] prices = {450, 600, 250, 180};
        for (int i = 0; i < items.length; i++) {
            MenuItem m = new MenuItem();
            m.setRestaurant(r);
            m.setItemName(items[i]);
            m.setPrice(prices[i]);
            m.setIsAvailable(true);
            repo.save(m);
        }
    }

    private void addTables(Restaurant r, RestaurantTableRepository repo) {
        int[] capacities = {2, 4, 6, 8};
        for (int i = 0; i < capacities.length; i++) {
            RestaurantTable t = new RestaurantTable();
            t.setRestaurant(r);
            t.setCapacity(capacities[i]);
            t.setPosX(50.0 + (i * 150));
            t.setPosY(100.0);
            t.setTableLabel("T" + (i + 1));
            t.setShape(i % 2 == 0 ? "round" : "square");
            repo.save(t);
        }
    }

    private void addSlots(Restaurant r, TimeSlotRepository repo) {
        LocalTime start = r.getOpeningTime();
        LocalTime end = r.getClosingTime();
        int count = 0;
        while (count < 20) { // Safety break
            LocalTime next = start.plusHours(2);
            if (next.isAfter(end) || next.isBefore(start) || next.equals(start)) break;
            
            TimeSlot s = new TimeSlot();
            s.setRestaurant(r);
            s.setStartTime(start);
            s.setEndTime(next);
            repo.save(s);
            start = next;
            count++;
        }
    }
}
