package com.example.bluetech.service.imp;

import com.example.bluetech.service.OpenAIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIServiceImpl implements OpenAIService {
    @Value("${openai.api.key}")
    private String openAIApiKey;

    @Value("${openai.api.url}")
    private String openAIApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Map<String, Object> generateFriendSuggestionScore(int mutualFriends,
                                                             double distance,
                                                             String userLocation,
                                                             String suggestionLocation) {
        try {
            String prompt = String.format(
                    "Analyze friend suggestion compatibility based on: " +
                            "Mutual friends: %d, Distance: %.2f km, " +
                            "User location: %s, Suggested friend location: %s. " +
                            "Generate a score from 0-100 and brief reason (max 50 words). " +
                            "Respond in JSON format: {\"score\": number, \"reason\": \"string\"}",
                    mutualFriends, distance, userLocation, suggestionLocation
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAIApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", 150);
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(openAIApiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String content = jsonResponse.path("choices").get(0).path("message").path("content").asText();

                // Parse the JSON response from OpenAI
                JsonNode aiResponse = objectMapper.readTree(content);
                double score = aiResponse.path("score").asDouble(50.0);
                String reason = aiResponse.path("reason").asText("Based on mutual connections and proximity");

                Map<String, Object> result = new HashMap<>();
                result.put("score", score);
                result.put("reason", reason);
                return result;
            }
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
        }

        // Fallback scoring logic
        double score = calculateFallbackScore(mutualFriends, distance);
        String reason = generateFallbackReason(mutualFriends, distance);

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reason", reason);
        return result;
    }

    public double calculateFallbackScore(int mutualFriends, double distance) {
        double mutualScore = Math.min(mutualFriends * 15, 60); // Max 60 for mutual friends
        double distanceScore = Math.max(40 - distance, 0); // Max 40 for distance
        return mutualScore + distanceScore;
    }

    public String generateFallbackReason(int mutualFriends, double distance) {
        if (mutualFriends > 3 && distance < 10) {
            return "High mutual connections and close proximity";
        } else if (mutualFriends > 2) {
            return "Good mutual friend connections";
        } else if (distance < 5) {
            return "Very close location";
        } else {
            return "Potential connection based on location";
        }
    }
}
