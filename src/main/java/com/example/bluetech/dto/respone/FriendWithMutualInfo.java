package com.example.bluetech.dto.respone;


import com.example.bluetech.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendWithMutualInfo {
    private String id;
    private String userName;
    private String email;
    private String avatarUrl;
    private String phone;
    private String gender;
    private int mutualFriendsCount;
    private List<User> mutualFriends;

    public static FriendWithMutualInfo fromUser(User user, int mutualCount, List<User> mutualFriends) {
        FriendWithMutualInfo dto = new FriendWithMutualInfo();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());
        dto.setMutualFriendsCount(mutualCount);
        dto.setMutualFriends(mutualFriends);
        return dto;
    }
}
