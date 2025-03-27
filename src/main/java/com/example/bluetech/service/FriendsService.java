package com.example.bluetech.service;

import com.example.bluetech.entity.Friends;
import com.example.bluetech.entity.User;

import java.util.List;

public interface FriendsService {
    Friends save(Friends friends);
    Friends  add(Friends friends);
    Friends  add(String user1, String user2 );
    List<Friends> findAll();
    List<String> findFriendIdByUserId(String userId);

}
