package com.dineelite.backend.service;
import com.dineelite.backend.dto.*;
import com.dineelite.backend.entity.*;
import com.dineelite.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dineelite.backend.enums.BookingStatus;
import com.dineelite.backend.enums.PaymentStatus;
import com.dineelite.backend.enums.NotificationType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingTableRepository bookingTableRepository;
    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final MenuItemRepository menuItemRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                          BookingTableRepository bookingTableRepository,
                          RestaurantTableRepository tableRepository,
                          RestaurantRepository restaurantRepository,
                          UserRepository userRepository,
                          TimeSlotRepository timeSlotRepository,
                          MenuItemRepository menuItemRepository,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.bookingTableRepository = bookingTableRepository;
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.menuItemRepository = menuItemRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public BookingResponse createBooking(Integer userId,
                                Integer restaurantId,
                                LocalDate bookingDate,
                                Integer slotId,
                                Integer guestCount,
                                Integer preferredTableId) {

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        Optional<TimeSlot> slotOpt = timeSlotRepository.findById(slotId);

        if (userOpt.isEmpty() || restaurantOpt.isEmpty() || slotOpt.isEmpty()) {
            return new BookingResponse(
                    "Invalid input data",
                    null,
                    null,
                    null
            );

        }

        if (bookingDate.isBefore(LocalDate.now())) {
            return new BookingResponse(
                    "Cannot book past dates",
                    null,
                    null,
                    null
            );

        }

        // Prevent booking outside restaurant working hours
        TimeSlot slot = slotOpt.get();
        Restaurant restaurant = restaurantOpt.get();

        if (slot.getStartTime().isBefore(restaurant.getOpeningTime()) ||
            slot.getEndTime().isAfter(restaurant.getClosingTime())) {
            return new BookingResponse(
        "Selected slot is outside restaurant working hours",
        null,
        null,
        null
);

        }

        // Step 1: Get all tables of that restaurant
        List<RestaurantTable> allTables =
                tableRepository.findAll()
                        .stream()
                        .filter(t -> t.getRestaurant().getRestaurantId().equals(restaurantId))
                        .collect(Collectors.toList());

        // Step 2: Filter by capacity
        List<RestaurantTable> suitableTables =
                allTables.stream()
                        .filter(t -> t.getCapacity() >= guestCount)
                        .sorted(Comparator.comparing(RestaurantTable::getCapacity))
                        .collect(Collectors.toList());

        if (suitableTables.isEmpty()) {
            return new BookingResponse(
                    "No table with sufficient capacity",
                    null,
                    null,
                    null
            );
        }

        // Step 3: Remove already booked tables
        List<BookingTable> bookedTables =
                bookingTableRepository
                .findByBookingDateAndSlot_SlotIdAndBooking_Status(
                        bookingDate,
                        slotId,
                        BookingStatus.CONFIRMED
                );
        List<Integer> bookedTableIds =
                bookedTables.stream()
                        .map(bt -> bt.getTable().getTableId())
                        .collect(Collectors.toList());

        List<RestaurantTable> availableTables =
                suitableTables.stream()
                        .filter(t -> !bookedTableIds.contains(t.getTableId()))
                        .collect(Collectors.toList());

        if (availableTables.isEmpty()) {
            return new BookingResponse(
                    "No available tables for selected slot",
                    null,
                    null,
                    null
            );
        }

        // Step 4: Choose table
        RestaurantTable selectedTable = null;

        if (preferredTableId != null) {
            Optional<RestaurantTable> preferredOpt = availableTables.stream()
                    .filter(t -> t.getTableId().equals(preferredTableId))
                    .findFirst();

            if (preferredOpt.isPresent()) {
                selectedTable = preferredOpt.get();
            } else {
                // Check if it's even in suitable tables but just booked
                boolean isBooked = bookedTableIds.contains(preferredTableId);
                boolean hasCapacity = allTables.stream().anyMatch(t -> t.getTableId().equals(preferredTableId) && t.getCapacity() >= guestCount);
                
                String errorMsg = "Selected table is not available";
                if (isBooked) errorMsg = "Selected table is already booked for this slot";
                else if (!hasCapacity) errorMsg = "Selected table does not have enough capacity for " + guestCount + " guests";

                return new BookingResponse(
                    errorMsg,
                    null,
                    null,
                    null
                );
            }
        } else {
            // Auto-assign: Choose smallest suitable table
            selectedTable = availableTables.get(0);
        }

        // Step 5: Save booking
        Booking booking = new Booking();
        booking.setUser(userOpt.get());
        booking.setRestaurant(restaurantOpt.get());
        booking.setBookingDate(bookingDate);

        booking.setSlot(slotOpt.get());
        booking.setGuestCount(guestCount);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setDepositAmount(restaurant.getDepositAmount());
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setCreatedAt(java.time.LocalDateTime.now());
        booking.setGuestDietaryNotes(userOpt.get().getDietaryPreferences());

        bookingRepository.save(booking);

        // Step 6: Save booking table mapping
        BookingTable bookingTable = new BookingTable();
        bookingTable.setBooking(booking);
        bookingTable.setTable(selectedTable);
        bookingTable.setBookingDate(bookingDate);
        bookingTable.setSlot(slotOpt.get());

        bookingTableRepository.save(bookingTable);

        // Notify Admin
        notificationService.createNotification(
            restaurant.getAdmin(),
            userOpt.get(),
            NotificationType.BOOKING,
            "New Booking! " + userOpt.get().getFullName() + " booked a table for " + guestCount + 
            " people at " + slotOpt.get().getStartTime() + " on " + bookingDate + 
            (booking.getGuestDietaryNotes() != null && !booking.getGuestDietaryNotes().isEmpty() ? 
             " | Guest Notes: " + booking.getGuestDietaryNotes() : "")
        );

        return new BookingResponse(
            "Booking successful",
            booking.getBookingId(),
            selectedTable.getTableId(),
            booking.getStatus().name()
    );
    }
    @Transactional
    public CancelResponse cancelBooking(Integer bookingId) {

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);

        if (bookingOpt.isEmpty()) {
            return new CancelResponse(
                    "Booking not found",
                    bookingId,
                    null
            );
        }

        Booking booking = bookingOpt.get();

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return new CancelResponse(
                    "Booking already cancelled",
                    bookingId,
                    BookingStatus.CANCELLED.name()
            );
        }

        // 🔥 Cancellation policy (1 hour before slot start)
        LocalDate bookingDate = booking.getBookingDate();
        TimeSlot slot = booking.getSlot();

        java.time.LocalDateTime bookingDateTime =
                java.time.LocalDateTime.of(bookingDate, slot.getStartTime());

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (bookingDateTime.minusHours(1).isBefore(now)) {
            return new CancelResponse(
                    "Cannot cancel within 1 hour of booking time",
                    bookingId,
                    booking.getStatus().name()
            );
        }

        // Change status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Release table mapping
        List<BookingTable> mappings =
                bookingTableRepository.findAll()
                        .stream()
                        .filter(bt -> bt.getBooking().getBookingId().equals(bookingId))
                        .collect(Collectors.toList());

        bookingTableRepository.deleteAll(mappings);

        // Notify Admin
        notificationService.createNotification(
            booking.getRestaurant().getAdmin(),
            booking.getUser(),
            NotificationType.BOOKING,
            "Booking Cancelled: " + booking.getUser().getFullName() + " for " + booking.getBookingDate()
        );

        return new CancelResponse(
                "Booking cancelled successfully",
                bookingId,
                BookingStatus.CANCELLED.name()
        );
    }

    public List<com.dineelite.backend.dto.UserBookingResponse> getUserBookingsDTO(Integer userId) {
        return bookingRepository.findByUser_UserIdOrderByBookingDateDesc(userId)
                .stream()
                .map(b -> new com.dineelite.backend.dto.UserBookingResponse(
                        b.getBookingId(),
                        b.getRestaurant().getName(),
                        b.getRestaurant().getAddress(),
                        b.getBookingDate(),
                        b.getSlot().getStartTime(),
                        b.getSlot().getEndTime(),
                        b.getGuestCount(),
                        b.getStatus().name(),
                        b.getDepositAmount(),
                        b.getPaymentStatus().name(),
                        b.getGuestDietaryNotes()
                ))
                .collect(Collectors.toList());
    }

    public List<com.dineelite.backend.dto.MenuItemResponse> getPopularMenu(Integer restaurantId) {
        return menuItemRepository.findByRestaurantRestaurantIdAndIsAvailableTrue(restaurantId)
                .stream()
                .map(m -> new com.dineelite.backend.dto.MenuItemResponse(
                        m.getMenuId(),
                        m.getItemName(),
                        m.getPrice(),
                        m.getIsAvailable()
                ))
                .collect(Collectors.toList());
    }

    @Deprecated
    public String getUserBookings(Integer userId) {

        List<Booking> bookings =
                bookingRepository.findByUser_UserIdOrderByBookingDateDesc(userId);

        if (bookings.isEmpty()) {
            return "No bookings found for this user";
        }

        StringBuilder response = new StringBuilder();

        for (Booking booking : bookings) {

            response.append("Booking ID: ").append(booking.getBookingId())
                    .append(" | Restaurant: ").append(booking.getRestaurant().getName())
                    .append(" | Date: ").append(booking.getBookingDate())
                    .append(" | Slot: ").append(booking.getSlot().getStartTime())
                    .append("-").append(booking.getSlot().getEndTime())
                    .append(" | Guests: ").append(booking.getGuestCount())
                    .append(" | Status: ").append(booking.getStatus())
                    .append(" | Payment: ").append(booking.getPaymentStatus())
                    .append("\n");
        }

        return response.toString();
    }
    public String getTotalBookingsForRestaurant(Integer restaurantId) {

        Long count = bookingRepository.countByRestaurant_RestaurantId(restaurantId);

        return "Total bookings for restaurant " + restaurantId + " : " + count;
    }

    public String getPeakSlotForRestaurant(Integer restaurantId) {

        List<Object[]> results =
                bookingRepository.findPeakSlotByRestaurant(restaurantId);

        if (results.isEmpty()) {
            return "No bookings found for this restaurant";
        }

        Object[] topResult = results.get(0);

        Integer slotId = (Integer) topResult[0];
        Long bookingCount = (Long) topResult[1];

        Optional<TimeSlot> slotOpt = timeSlotRepository.findById(slotId);

        if (slotOpt.isEmpty()) {
            return "Slot data not found";
        }

        TimeSlot slot = slotOpt.get();

        return "Peak Slot: "
                + slot.getStartTime()
                + "-"
                + slot.getEndTime()
                + " ("
                + bookingCount
                + " bookings)";
    }

    public String getDailyBookingTrend(Integer restaurantId) {

        List<Object[]> results =
                bookingRepository.findDailyBookingTrend(restaurantId);

        if (results.isEmpty()) {
            return "No confirmed bookings found for this restaurant";
        }

        StringBuilder response = new StringBuilder();

        for (Object[] row : results) {

            LocalDate date = (LocalDate) row[0];
            Long count = (Long) row[1];

            response.append(date)
                    .append(" : ")
                    .append(count)
                    .append(" bookings")
                    .append("\n");
        }

        return response.toString();
    }

    public String getTotalRevenue(Integer restaurantId) {

        Double revenue = bookingRepository.calculateTotalRevenue(restaurantId);

        if (revenue == null) {
            revenue = 0.0;
        }

        return "Total Revenue: ₹" + revenue;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> checkAvailability(Integer restaurantId,
                                                        LocalDate bookingDate,
                                                        Integer slotId,
                                                        Integer guestCount) {

        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        Optional<TimeSlot> slotOpt = timeSlotRepository.findById(slotId);

        if (restaurantOpt.isEmpty() || slotOpt.isEmpty()) {
            return List.of();
        }

        // Get all tables of restaurant
        List<RestaurantTable> tables =
                tableRepository.findAll()
                        .stream()
                        .filter(t -> t.getRestaurant().getRestaurantId().equals(restaurantId))
                        .collect(Collectors.toList());

        // Already booked tables for that date & slot
        List<BookingTable> bookedTables =
                bookingTableRepository
                    .findByBookingDateAndSlot_SlotIdAndBooking_Status(
                        bookingDate,
                        slotId,
                        BookingStatus.CONFIRMED
                    );

        List<Integer> bookedIds =
                bookedTables.stream()
                        .map(bt -> bt.getTable().getTableId())
                        .collect(Collectors.toList());

        return tables.stream()
                .map(t -> new AvailabilityResponse(
                        t.getTableId(),
                        t.getCapacity(),
                        bookedIds.contains(t.getTableId()),
                        t.getCapacity() >= guestCount
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getAvailableTimeSlots(Integer restaurantId,
                                                        LocalDate date,
                                                        Integer guestCount) {

        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        if (restaurantOpt.isEmpty()) return List.of();

        Restaurant restaurant = restaurantOpt.get();

        // 1. Get all slots within restaurant hours
        List<TimeSlot> allSlots = timeSlotRepository.findAll().stream()
                .filter(s -> !s.getStartTime().isBefore(restaurant.getOpeningTime()) &&
                             !s.getEndTime().isAfter(restaurant.getClosingTime()))
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .collect(Collectors.toList());

        // 2. Get tables with enough capacity
        List<RestaurantTable> suitableTables = tableRepository.findAll().stream()
                .filter(t -> t.getRestaurant().getRestaurantId().equals(restaurantId) &&
                             t.getCapacity() >= guestCount)
                .collect(Collectors.toList());

        if (suitableTables.isEmpty()) return List.of();

        List<TimeSlotResponse> availableSlots = new ArrayList<>();

        for (TimeSlot slot : allSlots) {
            // Check if ANY suitable table is free for this slot
            long bookedCount = bookingRepository.countConfirmedForSlot(restaurantId, date, slot.getSlotId());
            
            // If booked count < total suitable tables, at least one is free
            // rigorous check: check if specific suitable tables are available
             List<BookingTable> bookedInSlot = bookingTableRepository
                    .findByBookingDateAndSlot_SlotIdAndBooking_Status(
                        date,
                        slot.getSlotId(),
                        BookingStatus.CONFIRMED
                    );
            
            List<Integer> bookedTableIds = bookedInSlot.stream()
                    .map(bt -> bt.getTable().getTableId())
                    .collect(Collectors.toList());

            boolean anyTableFree = suitableTables.stream()
                    .anyMatch(t -> !bookedTableIds.contains(t.getTableId()));

            if (anyTableFree) {
                availableSlots.add(new TimeSlotResponse(
                        slot.getSlotId(),
                        slot.getStartTime(),
                        slot.getEndTime()
                ));
            }
        }

        return availableSlots;
    }

    public String getTableUtilization(Integer restaurantId,
                                  LocalDate bookingDate,
                                  Integer slotId) {

        // Total tables in restaurant
        Long totalTables = tableRepository.findAll()
                .stream()
                .filter(t -> t.getRestaurant().getRestaurantId().equals(restaurantId))
                .count();

        if (totalTables == 0) {
            return "No tables found for this restaurant";
        }

        // Confirmed bookings for that slot
        Long confirmedBookings =
                bookingRepository.countConfirmedForSlot(
                        restaurantId,
                        bookingDate,
                        slotId
                );

        double utilization =
                ((double) confirmedBookings / totalTables) * 100;

        return "Table Utilization: "
                + String.format("%.2f", utilization)
                + "%";
    }

    public DashboardAnalyticsDTO getDashboardAnalytics(Integer restaurantId) {
        DashboardAnalyticsDTO dto = new DashboardAnalyticsDTO();

        // 1. Total Bookings
        dto.setTotalBookings(bookingRepository.countByRestaurant_RestaurantId(restaurantId));

        // 2. Peak Slot
        dto.setPeakSlot(getPeakSlotForRestaurant(restaurantId).replace("Peak Slot: ", ""));

        // 3. Total Revenue
        Double revenue = bookingRepository.calculateTotalRevenue(restaurantId);
        dto.setTotalRevenue(revenue != null ? revenue : 0.0);

        // 4. Daily Trends
        List<Object[]> trendResults = bookingRepository.findDailyBookingTrend(restaurantId);
        List<DailyTrendDTO> dailyTrends = trendResults.stream()
                .map(row -> new DailyTrendDTO((LocalDate) row[0], (Long) row[1]))
                .collect(Collectors.toList());
        dto.setDailyTrends(dailyTrends);

        // 5. Slot Utilization & Average Utilization
        List<TimeSlot> allSlots = timeSlotRepository.findAll();
        List<SlotUtilizationDTO> slotUtilizations = new ArrayList<>();
        double totalUtilization = 0;
        int slotCount = 0;

        // Current date for utilization check (could be parameterized later)
        LocalDate today = LocalDate.now();

        // Total tables in restaurant
        Long totalTables = tableRepository.findAll()
                .stream()
                .filter(t -> t.getRestaurant().getRestaurantId().equals(restaurantId))
                .count();

        if (totalTables > 0) {
            for (TimeSlot slot : allSlots) {
                Long confirmed = bookingRepository.countConfirmedForSlot(restaurantId, today, slot.getSlotId());
                double utilization = ((double) confirmed / totalTables) * 100;
                slotUtilizations.add(new SlotUtilizationDTO(
                        slot.getStartTime() + " - " + slot.getEndTime(),
                        utilization
                ));
                totalUtilization += utilization;
                slotCount++;
            }
        }

        dto.setSlotUtilizations(slotUtilizations);
        dto.setAverageUtilization(slotCount > 0 ? (totalUtilization / slotCount) : 0.0);

        // 6. Booking Heatmap
        List<Object[]> heatmapResults = bookingRepository.findHeatmapData(restaurantId);
        List<HeatmapDataDTO> heatmapData = heatmapResults.stream()
                .map(row -> {
                    // row[0] is DOW (double/numeric from Postgres), row[1] is start_time (Time), row[2] is count (long)
                    Integer dow = ((Number) row[0]).intValue();
                    String time = row[1].toString();
                    Long count = ((Number) row[2]).longValue();
                    return new HeatmapDataDTO(dow, time, count);
                })
                .collect(Collectors.toList());
        dto.setBookingHeatmap(heatmapData);

        return dto;
    }


}
