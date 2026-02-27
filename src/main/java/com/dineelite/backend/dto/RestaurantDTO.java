package com.dineelite.backend.dto;

import java.time.LocalTime;

public class RestaurantDTO {

    private Integer id;
    private String name;
    private String address;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String imageUrl;
    private Double depositAmount;
    private String description;
    private String houseRules;
    private String cuisine;
    private Double latitude;
    private Double longitude;

    public RestaurantDTO(Integer id,
                         String name,
                         String address,
                         LocalTime openingTime,
                         LocalTime closingTime,
                         String imageUrl,
                         Double depositAmount,
                         String description,
                         String houseRules,
                         String cuisine,
                         Double latitude,
                         Double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.imageUrl = imageUrl;
        this.depositAmount = depositAmount;
        this.description = description;
        this.houseRules = houseRules;
        this.cuisine = cuisine;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public LocalTime getOpeningTime() { return openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public String getImageUrl() { return imageUrl; }
    public Double getDepositAmount() { return depositAmount; }
    public String getDescription() { return description; }
    public String getHouseRules() { return houseRules; }
    public String getCuisine() { return cuisine; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}
