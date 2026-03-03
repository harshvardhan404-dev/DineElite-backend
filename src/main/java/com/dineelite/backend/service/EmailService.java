package com.dineelite.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class EmailService {

    @Value("${BACKEND_URL:http://localhost:8081}")
    private String backendUrl;

    @Value("${BREVO_API_KEY:NOT_SET}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendVerificationEmail(String to, String token) {
        String url = backendUrl + "/api/verify?token=" + token;
        
        System.out.println(">>> Attempting to send email via Brevo API to: " + to);
        
        if ("NOT_SET".equals(brevoApiKey) || brevoApiKey.isEmpty()) {
            System.out.println(">>> [WARNING] BREVO_API_KEY not configured. Email NOT sent.");
            System.out.println(">>> [LOG] To: " + to);
            System.out.println(">>> [LOG] Verification Link: " + url);
            return;
        }

        String brevoApiUrl = "https://api.brevo.com/v3/smtp/email";

        // Prepare Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        // Prepare Body
        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", "DineElite", "email", "harshvardhansonawane2@gmail.com"));
        body.put("to", List.of(Map.of("email", to)));
        body.put("subject", "Verify your DineElite Account");
        body.put("htmlContent", "<html><body>" +
                "<h1>Welcome to DineElite!</h1>" +
                "<p>Please click the link below to verify your account:</p>" +
                "<a href=\"" + url + "\">Verify Account</a>" +
                "<p>Or copy this link: " + url + "</p>" +
                "</body></html>");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(brevoApiUrl, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println(">>> Verification email sent successfully via Brevo to: " + to);
            } else {
                System.err.println(">>> Brevo API returned error: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println(">>> Failed to send email via Brevo: " + e.getMessage());
        }
    }
}
