package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUser_UserIdAndAdvertisement_AdId(Integer userId, Integer adId);
    Long countByAdvertisement_AdId(Integer adId);
    void deleteByUser(com.dineelite.backend.entity.User user);
    void deleteByAdvertisement(com.dineelite.backend.entity.Advertisement advertisement);
}
