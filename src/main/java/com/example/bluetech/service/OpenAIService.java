package com.example.bluetech.service;

import java.util.Map;

public interface OpenAIService {
    Map<String, Object> generateFriendSuggestionScore(int mutualFriends,
                                                             double distance,
                                                             String userLocation,
                                                             String suggestionLocation);

    double calculateFallbackScore(int mutualFriends, double distance);
    String generateFallbackReason(int mutualFriends, double distance);
}
