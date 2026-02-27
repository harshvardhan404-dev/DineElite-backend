package com.dineelite.backend.service;

import com.dineelite.backend.entity.Comment;
import com.dineelite.backend.entity.Like;
import com.dineelite.backend.entity.Advertisement;
import com.dineelite.backend.entity.User;
import com.dineelite.backend.enums.NotificationType;
import com.dineelite.backend.repository.CommentRepository;
import com.dineelite.backend.repository.LikeRepository;
import com.dineelite.backend.repository.AdvertisementRepository;
import com.dineelite.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SocialService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SocialService(LikeRepository likeRepository,
                         CommentRepository commentRepository,
                         AdvertisementRepository advertisementRepository,
                         UserRepository userRepository,
                         NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.advertisementRepository = advertisementRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void toggleLike(Integer userId, Integer adId) {
        User user = userRepository.findById(userId).orElseThrow();
        Advertisement ad = advertisementRepository.findById(adId).orElseThrow();

        Optional<Like> existingLike = likeRepository.findByUser_UserIdAndAdvertisement_AdId(userId, adId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setAdvertisement(ad);
            likeRepository.save(like);

            // Notify Admin
            notificationService.createNotification(
                ad.getRestaurant().getAdmin(),
                user,
                NotificationType.LIKE,
                user.getFullName() + " liked your post: " + ad.getCaption()
            );
        }
    }

    @Transactional
    public Comment addComment(Integer userId, Integer adId, String content) {
        User user = userRepository.findById(userId).orElseThrow();
        Advertisement ad = advertisementRepository.findById(adId).orElseThrow();

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setAdvertisement(ad);
        comment.setContent(content);
        Comment savedComment = commentRepository.save(comment);

        // Notify Admin
        notificationService.createNotification(
            ad.getRestaurant().getAdmin(),
            user,
            NotificationType.COMMENT,
            user.getFullName() + " commented on your post: " + content
        );

        return savedComment;
    }

    public List<Comment> getCommentsForAd(Integer adId) {
        return commentRepository.findByAdvertisement_AdIdOrderByCreatedAtDesc(adId);
    }

    public Long getLikeCount(Integer adId) {
        return likeRepository.countByAdvertisement_AdId(adId);
    }

    public boolean isLikedByUser(Integer userId, Integer adId) {
        return likeRepository.findByUser_UserIdAndAdvertisement_AdId(userId, adId).isPresent();
    }
}
