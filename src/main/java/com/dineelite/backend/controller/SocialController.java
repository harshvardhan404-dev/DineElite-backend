package com.dineelite.backend.controller;

import com.dineelite.backend.dto.CommentResponse;
import com.dineelite.backend.service.SocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
@CrossOrigin(origins = "*")
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/like")
    public ResponseEntity<?> toggleLike(@RequestParam Integer userId, @RequestParam Integer adId) {
        socialService.toggleLike(userId, adId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment")
    public ResponseEntity<CommentResponse> addComment(@RequestParam Integer userId, @RequestParam Integer adId, @RequestBody String content) {
        // Remove quotes if any from the request body string
        String cleanContent = content.startsWith("\"") && content.endsWith("\"") ? content.substring(1, content.length()-1) : content;
        var comment = socialService.addComment(userId, adId, cleanContent);
        return ResponseEntity.ok(new CommentResponse(
            comment.getCommentId(),
            comment.getUser().getFullName(),
            comment.getContent(),
            comment.getCreatedAt()
        ));
    }

    @GetMapping("/comments/{adId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Integer adId) {
        var comments = socialService.getCommentsForAd(adId).stream()
            .map(c -> new CommentResponse(c.getCommentId(), c.getUser().getFullName(), c.getContent(), c.getCreatedAt()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/ad-stats/{adId}")
    public ResponseEntity<?> getAdStats(@PathVariable Integer adId, @RequestParam(required = false) Integer userId) {
        Long likes = socialService.getLikeCount(adId);
        boolean liked = userId != null && socialService.isLikedByUser(userId, adId);
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("likes", likes);
        stats.put("isLiked", liked);
        return ResponseEntity.ok(stats);
    }
}
