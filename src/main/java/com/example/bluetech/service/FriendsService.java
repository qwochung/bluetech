package com.example.bluetech.service;

import com.example.bluetech.dto.request.FriendSuggestionRequest;
import com.example.bluetech.dto.respone.FriendSuggestionResponse;
import com.example.bluetech.entity.Address;
import com.example.bluetech.entity.Friends;
import com.example.bluetech.entity.User;

import java.util.List;
import java.util.Map;

public interface FriendsService {
    Friends save(Friends friends);
    Friends  add(Friends friends);
    Friends  add(String user1, String user2 );
    List<Friends> findAll();
    List<String> findFriendIdByUserId(String userId);
    void delete(String user1, String user2);
    List<String> findMutualFriends(String userId1, String userId2);
    Map<String, Integer> countMutualFriendsForMultipleUsers(String currentUserId, List<String> targetUserIds);
    Map<String, List<String>> findMutualFriendsForMultipleUsers(String currentUserId, List<String> targetUserIds);

    List<FriendSuggestionResponse> suggestFriends(FriendSuggestionRequest request);
    List<User> calculateMutualFriends(String userId1, String userId2);
    double calculateDistance(Address address1, Address address2);
    double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2);
    String getLocationString(Address address);

}
