package com.dineelite.backend.service;

import com.dineelite.backend.dto.AdvertisementResponse;
import com.dineelite.backend.entity.Advertisement;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.repository.AdvertisementRepository;
import com.dineelite.backend.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<AdvertisementResponse> getAllAdvertisements() {
        return advertisementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AdvertisementResponse> getAdvertisementsByRestaurant(Integer restaurantId) {
        return advertisementRepository.findByRestaurantRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AdvertisementResponse createAdvertisement(Advertisement ad) {
        Advertisement savedAd = advertisementRepository.save(ad);
        return mapToResponse(savedAd);
    }

    private AdvertisementResponse mapToResponse(Advertisement ad) {
        AdvertisementResponse response = new AdvertisementResponse();
        response.setAdId(ad.getAdId());
        response.setRestaurantId(ad.getRestaurant().getRestaurantId());
        response.setRestaurantName(ad.getRestaurant().getName());
        response.setMediaUrl(ad.getMediaUrl());
        response.setMediaType(ad.getMediaType());
        response.setCaption(ad.getCaption());
        response.setCreatedAt(ad.getCreatedAt());
        return response;
    }
}
