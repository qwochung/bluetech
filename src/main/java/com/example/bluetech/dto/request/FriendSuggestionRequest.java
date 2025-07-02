package com.example.bluetech.dto.request;

import lombok.Data;

@Data
public class FriendSuggestionRequest {
    private String userId;
    private int limit = 10; // Default limit
    private double maxDistance = 50.0; // Max distance in km
}
