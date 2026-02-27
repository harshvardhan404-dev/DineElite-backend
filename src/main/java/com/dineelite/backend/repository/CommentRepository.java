package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAdvertisement_AdIdOrderByCreatedAtDesc(Integer adId);
    Long countByAdvertisement_AdId(Integer adId);
    void deleteByUser(com.dineelite.backend.entity.User user);
    void deleteByAdvertisement(com.dineelite.backend.entity.Advertisement advertisement);
}
