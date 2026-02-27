package com.dineelite.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SecurityReproductionTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void publicEndpointsShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk());
    }

    @Test
    public void adminEndpointShouldBeForbiddenForAnonymous() throws Exception {
        mockMvc.perform(get("/booking/admin/restaurant/1/count"))
                .andExpect(status().isUnauthorized()); // Now receiving 401 as expected
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void adminEndpointShouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(get("/booking/admin/restaurant/1/count"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void adminEndpointShouldBeAccessibleForAdmin() throws Exception {
        mockMvc.perform(get("/booking/admin/restaurant/1/count"))
                .andExpect(status().isOk());
    }
}
