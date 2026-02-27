package com.dineelite.backend.controller;

import com.dineelite.backend.dto.ReviewRequest;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.entity.User;
import com.dineelite.backend.repository.RestaurantRepository;
import com.dineelite.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class ReviewControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private ObjectMapper objectMapper;

    private User testUser;
    private Restaurant testRestaurant;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register modules like JavaTimeModule if present

        // Ensure we have a user and a restaurant
        testUser = userRepository.findByEmail("rahul@test.com").orElseGet(() -> {
            User user = new User();
            user.setFullName("Test User");
            user.setEmail("rahul@test.com");
            user.setPassword("pass");
            user.setRole("CUSTOMER");
            return userRepository.save(user);
        });

        testRestaurant = restaurantRepository.findAll().stream().findFirst().orElseGet(() -> {
            Restaurant r = new Restaurant();
            r.setName("Test Restaurant");
            r.setAddress("Test Address");
            r.setAdmin(testUser); // Just for test
            return restaurantRepository.save(r);
        });
    }

    @Test
    public void testAddReviewWithPhotos() throws Exception {
        ReviewRequest request = new ReviewRequest();
        request.setUserId(testUser.getUserId());
        request.setRestaurantId(testRestaurant.getRestaurantId());
        request.setRating(5);
        request.setContent("Great experience!");

        String reviewJson = objectMapper.writeValueAsString(request);
        MockMultipartFile reviewPart = new MockMultipartFile(
                "review",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                reviewJson.getBytes()
        );

        MockMultipartFile photo1 = new MockMultipartFile(
                "photos",
                "test1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image content 1".getBytes()
        );

        MockMultipartFile photo2 = new MockMultipartFile(
                "photos",
                "test2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image content 2".getBytes()
        );

        mockMvc.perform(multipart("/api/reviews")
                        .file(reviewPart)
                        .file(photo1)
                        .file(photo2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.content").value("Great experience!"))
                .andExpect(jsonPath("$.photoUrls.length()").value(2));
    }
}
