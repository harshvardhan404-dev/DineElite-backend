package com.dineelite.backend.repository;

import com.dineelite.backend.entity.BookingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dineelite.backend.enums.BookingStatus;
import java.time.LocalDate;
import java.util.List;

public interface BookingTableRepository extends JpaRepository<BookingTable, Integer> {

    List<BookingTable> findByBookingDateAndSlot_SlotIdAndBooking_Status(
        LocalDate bookingDate,
        Integer slotId,
        BookingStatus status
);

    void deleteByBooking(com.dineelite.backend.entity.Booking booking);
}

