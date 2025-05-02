package com.example.bluetech.service;

public interface EmailService {
    void sendMailWithLink(String to, String subject, String templateName, String userName, Object value);
}
