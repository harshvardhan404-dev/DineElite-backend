package com.dineelite.backend.repository;

import com.dineelite.backend.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, Integer> {
    List<BookingItem> findByBookingBookingId(Integer bookingId);
}
