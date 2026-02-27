package com.dineelite.backend.dto;

import com.dineelite.backend.enums.MediaType;

public class AdvertisementRequest {
    private Integer restaurantId;
    private String mediaUrl;
    private MediaType mediaType;
    private String caption;

    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
}
