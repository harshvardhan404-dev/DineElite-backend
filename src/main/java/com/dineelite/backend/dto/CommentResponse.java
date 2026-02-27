package com.dineelite.backend.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    private Integer commentId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;

    public CommentResponse(Integer commentId, String userName, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.userName = userName;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters
    public Integer getCommentId() { return commentId; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
