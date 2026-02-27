package com.dineelite.backend.controller;

import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.entity.RestaurantTable;
import com.dineelite.backend.repository.RestaurantRepository;
import com.dineelite.backend.repository.RestaurantTableRepository;
import com.dineelite.backend.service.BookingService;
import com.dineelite.backend.dto.AvailabilityResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/table-layout")
public class TableLayoutController {

    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final BookingService bookingService;

    public TableLayoutController(RestaurantTableRepository tableRepository,
                                  RestaurantRepository restaurantRepository,
                                  BookingService bookingService) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
        this.bookingService = bookingService;
    }

    /**
     * Get availability for all tables in a restaurant for a specific slot.
     */
    @GetMapping("/{restaurantId}/availability")
    public ResponseEntity<List<Map<String, Object>>> getAvailability(
            @PathVariable Integer restaurantId,
            @RequestParam String date,
            @RequestParam Integer slotId,
            @RequestParam Integer guestCount) {
        
        // 1. Get current layout positions
        List<RestaurantTable> allTables = tableRepository.findByRestaurant_RestaurantId(restaurantId);
        
        // 2. Get availability status from BookingService
        List<AvailabilityResponse> availability = bookingService.checkAvailability(
                restaurantId, 
                LocalDate.parse(date), 
                slotId, 
                guestCount
        );

        // 3. Merge availability data into layout data
        List<Map<String, Object>> result = allTables.stream().map(t -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("tableId", t.getTableId());
            map.put("capacity", t.getCapacity());
            map.put("posX", t.getPosX() != null ? t.getPosX() : 0.0);
            map.put("posY", t.getPosY() != null ? t.getPosY() : 0.0);
            map.put("tableLabel", t.getTableLabel() != null ? t.getTableLabel() : "T" + t.getTableId());
            map.put("shape", t.getShape() != null ? t.getShape() : "round");
            
            // Find availability
            Optional<AvailabilityResponse> availabilityItem = availability.stream()
                    .filter(a -> a.getTableId().equals(t.getTableId()))
                    .findFirst();
            
            map.put("isBooked", availabilityItem.map(AvailabilityResponse::isBooked).orElse(false));
            map.put("hasCapacity", availabilityItem.map(AvailabilityResponse::hasCapacity).orElse(false));
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Get all tables (with layout positions) for a restaurant.
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<Map<String, Object>>> getLayout(@PathVariable Integer restaurantId) {
        List<RestaurantTable> tables = tableRepository.findByRestaurant_RestaurantId(restaurantId);
        List<Map<String, Object>> result = tables.stream().map(t -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("tableId", t.getTableId());
            map.put("capacity", t.getCapacity());
            map.put("posX", t.getPosX() != null ? t.getPosX() : 0.0);
            map.put("posY", t.getPosY() != null ? t.getPosY() : 0.0);
            map.put("tableLabel", t.getTableLabel() != null ? t.getTableLabel() : "T" + t.getTableId());
            map.put("shape", t.getShape() != null ? t.getShape() : "round");
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Batch-update table positions/shapes/labels.
     */
    @PutMapping("/{restaurantId}")
    public ResponseEntity<?> saveLayout(@PathVariable Integer restaurantId,
                                         @RequestBody List<Map<String, Object>> tableUpdates) {
        for (Map<String, Object> update : tableUpdates) {
            Integer tableId = (Integer) update.get("tableId");
            if (tableId == null) continue;

            tableRepository.findById(tableId).ifPresent(table -> {
                if (update.containsKey("posX")) {
                    table.setPosX(((Number) update.get("posX")).doubleValue());
                }
                if (update.containsKey("posY")) {
                    table.setPosY(((Number) update.get("posY")).doubleValue());
                }
                if (update.containsKey("tableLabel")) {
                    table.setTableLabel((String) update.get("tableLabel"));
                }
                if (update.containsKey("shape")) {
                    table.setShape((String) update.get("shape"));
                }
                if (update.containsKey("capacity")) {
                    table.setCapacity(((Number) update.get("capacity")).intValue());
                }
                tableRepository.save(table);
            });
        }
        return ResponseEntity.ok(Map.of("message", "Layout saved successfully"));
    }

    /**
     * Add a new table to a restaurant.
     */
    @PostMapping("/{restaurantId}")
    public ResponseEntity<Map<String, Object>> addTable(@PathVariable Integer restaurantId,
                                                         @RequestBody Map<String, Object> body) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }

        RestaurantTable table = new RestaurantTable();
        table.setRestaurant(restaurant);
        table.setCapacity(body.containsKey("capacity") ? ((Number) body.get("capacity")).intValue() : 2);
        table.setPosX(body.containsKey("posX") ? ((Number) body.get("posX")).doubleValue() : 100.0);
        table.setPosY(body.containsKey("posY") ? ((Number) body.get("posY")).doubleValue() : 100.0);
        table.setTableLabel(body.containsKey("tableLabel") ? (String) body.get("tableLabel") : "New");
        table.setShape(body.containsKey("shape") ? (String) body.get("shape") : "round");

        RestaurantTable saved = tableRepository.save(table);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tableId", saved.getTableId());
        result.put("capacity", saved.getCapacity());
        result.put("posX", saved.getPosX());
        result.put("posY", saved.getPosY());
        result.put("tableLabel", saved.getTableLabel());
        result.put("shape", saved.getShape());
        return ResponseEntity.ok(result);
    }

    /**
     * Delete a table.
     */
    @DeleteMapping("/table/{tableId}")
    public ResponseEntity<?> deleteTable(@PathVariable Integer tableId) {
        if (!tableRepository.existsById(tableId)) {
            return ResponseEntity.notFound().build();
        }
        tableRepository.deleteById(tableId);
        return ResponseEntity.ok(Map.of("message", "Table deleted"));
    }
}
