package com.dineelite.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String url = "http://localhost:8081/api/verify?token=" + token;
        
        if (mailSender == null) {
            System.out.println(">>> [WARNING] JavaMailSender not configured. Email NOT sent.");
            System.out.println(">>> [LOG] To: " + to);
            System.out.println(">>> [LOG] Verification Link: " + url);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your DineElite Account");
        message.setText("Welcome to DineElite! \n\n" +
                "Please click the link below to verify your account: \n" +
                url + "\n\n" +
                "Thank you!");

        try {
            mailSender.send(message);
            System.out.println(">>> Verification email sent to: " + to);
        } catch (Exception e) {
            System.err.println(">>> Failed to send email: " + e.getMessage());
        }
    }
}
