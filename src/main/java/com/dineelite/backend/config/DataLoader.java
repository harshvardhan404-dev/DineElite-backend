package com.dineelite.backend.config;

import com.dineelite.backend.entity.*;
import com.dineelite.backend.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

            // Ensure new columns exist (to bypass ddl-auto update delays/fails)
            try {
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS is_enabled BOOLEAN DEFAULT TRUE");
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_token VARCHAR(255)");
                System.out.println(">>> Database columns checked/updated.");
            } catch (Exception e) {
                System.out.println(">>> Note: Column check skipped or failed: " + e.getMessage());
            }

            // 1. Create/Sync Default General Admin
            User mainAdmin = userRepository.findByEmail("admin@dineelite.com").orElse(new User());
            mainAdmin.setFullName("General Admin");
            mainAdmin.setEmail("admin@dineelite.com");
            if (mainAdmin.getPassword() == null || !mainAdmin.getPassword().startsWith("$2a$")) {
                mainAdmin.setPassword(passwordEncoder.encode("admin"));
                System.out.println(">>> Hashed General Admin: admin@dineelite.com");
            }
            mainAdmin.setRole("ADMIN");
            mainAdmin.setEnabled(true);
            userRepository.save(mainAdmin);
 
            // 2. Create/Sync Unique Restaurant Admins (admin1 to admin10)
            for (int i = 1; i <= 10; i++) {
                String adminEmail = "admin" + i + "@dineelite.com";
                String adminPass = "password" + i;
                User rAdmin = userRepository.findByEmail(adminEmail).orElse(new User());
                rAdmin.setFullName("Admin for Restaurant " + i);
                rAdmin.setEmail(adminEmail);
                if (rAdmin.getPassword() == null || !rAdmin.getPassword().startsWith("$2a$")) {
                    rAdmin.setPassword(passwordEncoder.encode(adminPass));
                    System.out.println(">>> Hashed Admin: " + adminEmail);
                }
                rAdmin.setRole("ADMIN");
                rAdmin.setEnabled(true);
                userRepository.save(rAdmin);
            }
 
            User customer = userRepository.findByEmail("rahul@test.com").orElse(new User());
            customer.setFullName("Rahul Kumar");
            customer.setEmail("rahul@test.com");
            if (customer.getPassword() == null || !customer.getPassword().startsWith("$2a$")) {
                customer.setPassword(passwordEncoder.encode("pass"));
                System.out.println(">>> Hashed Customer: rahul@test.com");
            }
            customer.setRole("CUSTOMER");
            customer.setEnabled(true);
            userRepository.save(customer);

            // Ensure existing restaurants have coordinates if missing (for legacy data)
            if (restaurantRepository.count() >= 10) {
                String[][] coordsData = {
                    {"Fine Dine Palace", "18.5204", "73.8567"},
                    {"The Spice Garden", "19.0760", "72.8777"},
                    {"Coastal Cravings", "15.2993", "74.1240"},
                    {"Zen Sushi Hub", "12.9716", "77.5946"},
                    {"Little Italy", "28.6139", "77.2090"},
                    {"Steak House Prime", "17.3850", "78.4867"},
                    {"The Breakfast Club", "13.0827", "80.2707"},
                    {"Dragon Bowl", "22.5726", "88.3639"},
                    {"Mediterranean Mist", "18.5204", "73.8567"},
                    {"Vibrant Veggie", "21.1702", "72.8311"}
                };
                
                for (String[] cd : coordsData) {
                    restaurantRepository.findAll().stream()
                        .filter(res -> res.getName().equals(cd[0]))
                        .findFirst()
                        .ifPresent(r -> {
                            if (r.getLatitude() == null) {
                                r.setLatitude(Double.parseDouble(cd[1]));
                                r.setLongitude(Double.parseDouble(cd[2]));
                                restaurantRepository.save(r);
                                System.out.println(">>> Updated legacy coordinates for: " + r.getName());
                            }
                        });
                }
                
                System.out.println(">>> Database already seeded. Skipping full data injection.");
                return;
            }

            System.out.println(">>> First run detected. Seeding database with initial data...");
            // Clear any partial/legacy data
            try {
                jdbcTemplate.execute("TRUNCATE TABLE subscriptions CASCADE");
            } catch (Exception e) {
                // Ignore if table doesn't exist
            }

            // Clear existing entries efficiently using TRUNCATE CASCADE
            try {
                jdbcTemplate.execute("TRUNCATE TABLE notifications, comments, likes, booking_tables, bookings, advertisements, menu_items, time_slots, restaurant_tables, restaurants CASCADE");
                System.out.println(">>> Database tables truncated successfully.");
            } catch (Exception e) {
                System.out.println(">>> Warning: Truncate failed, falling back to manual delete: " + e.getMessage());
                notificationRepository.deleteAllInBatch();
                commentRepository.deleteAllInBatch();
                likeRepository.deleteAllInBatch();
                bookingTableRepository.deleteAllInBatch();
                bookingRepository.deleteAllInBatch();
                advertisementRepository.deleteAllInBatch();
                menuItemRepository.deleteAllInBatch();
                timeSlotRepository.deleteAllInBatch();
                tableRepository.deleteAllInBatch();
                restaurantRepository.deleteAllInBatch();
            }
            
            // Reset Auto-Increment Sequences (PostgreSQL specific)
            try {
                jdbcTemplate.execute("ALTER SEQUENCE restaurants_restaurant_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE restaurant_tables_table_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE time_slots_slot_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE menu_items_menu_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE bookings_booking_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE booking_tables_booking_table_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE advertisements_ad_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE likes_like_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE comments_comment_id_seq RESTART WITH 1");
                jdbcTemplate.execute("ALTER SEQUENCE notifications_notification_id_seq RESTART WITH 1");
                System.out.println(">>> Auto-increment sequences reset to 1.");
            } catch (Exception e) {
                System.out.println(">>> Warning: Could not reset sequences (ignore if not using Postgres): " + e.getMessage());
            }

            // 4. Define Restaurants (10 Premium Entries)
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
                String[] data = restaurantData[i];
                
                // Fetch pre-synced admin for each restaurant
                String adminEmail = "admin" + (i + 1) + "@dineelite.com";
                User restaurantAdmin = userRepository.findByEmail(adminEmail).orElseThrow();

                Restaurant r = new Restaurant();
                r.setName(data[0]);
                r.setAddress(data[1]);
                r.setOpeningTime(LocalTime.parse(data[2]));
                r.setClosingTime(LocalTime.parse(data[3]));
                r.setImageUrl(data[4]);
                r.setDepositAmount(Double.parseDouble(data[5]));
                r.setDescription(data[6]);
                r.setCuisine(data[7]);
                r.setLatitude(Double.parseDouble(data[8]));
                r.setLongitude(Double.parseDouble(data[9]));
                r.setHouseRules("1. Smart casual dress code required.\n2. Please arrive 15 minutes before your slot.\n3. Outside food and drinks are not permitted.\n4. Table will be held for maximum 20 minutes.");
                r.setAdmin(restaurantAdmin);
                restaurantRepository.save(r);

                addMenu(r, menuItemRepository);
                addTables(r, tableRepository);
                addSlots(r, timeSlotRepository);
            }
            System.out.println(">>> 10 Premium Restaurants and data injected successfully.");
        };
    }

    private void addMenu(Restaurant r, MenuItemRepository repo) {
        String[] items = {"Signature Dish", "Chef's Special", "Popular Choice", "Classic Favorite", "Desert Delight"};
        double[] prices = {450, 600, 350, 250, 180};
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
        // Create 10 tables with varied capacities: 2, 4, 6, 8, 10
        int[] capacities = {2, 2, 4, 4, 4, 6, 6, 8, 8, 10};
        String[] shapes = {"round", "square", "rectangle", "round", "square", "rectangle", "round", "square", "rectangle", "round"};
        
        // Default positions for a spread-out layout
        double[][] positions = {
            {50, 50}, {250, 50}, {450, 50},
            {50, 250}, {250, 250}, {450, 250},
            {50, 450}, {250, 450}, {450, 450},
            {250, 550}
        };

        for (int i = 0; i < capacities.length; i++) {
            RestaurantTable t = new RestaurantTable();
            t.setRestaurant(r);
            t.setCapacity(capacities[i]);
            t.setPosX(positions[i][0]);
            t.setPosY(positions[i][1]);
            t.setTableLabel("T" + (i + 1));
            t.setShape(shapes[i]);
            repo.save(t);
        }
    }

    private void addSlots(Restaurant r, TimeSlotRepository repo) {
        LocalTime start = r.getOpeningTime();
        LocalTime close = r.getClosingTime();
        
        while (true) {
            LocalTime next = start.plusHours(2);
            
            // Check if next slot is valid (within same day and before/at closing)
            // If next < start, it crossed midnight
            if (next.isBefore(start) || next.isAfter(close)) {
                break;
            }
            
            TimeSlot s = new TimeSlot();
            s.setStartTime(start);
            s.setEndTime(next);
            repo.save(s);
            
            start = next;
            if (start.equals(close)) break;
        }
    }
}
