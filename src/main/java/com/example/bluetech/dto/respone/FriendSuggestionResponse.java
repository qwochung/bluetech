package com.example.bluetech.dto.respone;

import com.example.bluetech.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendSuggestionResponse {
    private String id;
    private String userName;
    private String email;
    private String avatarUrl;
    private String phone;
    private String gender;
    private int mutualFriendsCount;
    private List<User> mutualFriends;
    private double distance; // Distance in km
    private String location;
    private double suggestionScore; // AI-generated score
    private String suggestionReason; // AI-generated reason

    public static FriendSuggestionResponse fromUser(User user, int mutualCount,
                                                    List<User> mutualFriends,
                                                    double distance,
                                                    String location,
                                                    double score,
                                                    String reason) {
        FriendSuggestionResponse dto = new FriendSuggestionResponse();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());
        dto.setMutualFriendsCount(mutualCount);
        dto.setMutualFriends(mutualFriends);
        dto.setDistance(distance);
        dto.setLocation(location);
        dto.setSuggestionScore(score);
        dto.setSuggestionReason(reason);
        return dto;
    }
}
