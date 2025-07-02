package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.Status;
import com.example.bluetech.dto.request.FriendSuggestionRequest;
import com.example.bluetech.dto.respone.FriendSuggestionResponse;
import com.example.bluetech.entity.Address;
import com.example.bluetech.entity.Friends;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.FriendsRepository;
import com.example.bluetech.repository.UserRepository;
import com.example.bluetech.service.FriendsService;
import com.example.bluetech.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    @Autowired
    FriendsRepository friendsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    @Override
    public Friends save(Friends friends) {
        return friendsRepository.save(friends);
    }

    @Override
    public Friends add(Friends friends) {
        if (friends.getUser1() == null || friends.getUser2() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return friendsRepository.save(friends);
    }

    @Override
    public Friends add(String  user1, String  user2) {
        Friends friends = Friends.create(user1, user2);
        friends.setCreatedAt(System.currentTimeMillis());
        friends = friendsRepository.save(friends);
        return friends;
     }

    @Override
    public List<Friends> findAll() {
        return List.of();
    }

    @Override
    public List<String> findFriendIdByUserId(String userId) {
        Query query = new Query();

        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("status").is(Status.ACTIVE),
                new Criteria().orOperator(
                        Criteria.where("user1").is(userId),
                        Criteria.where("user2").is(userId)
                )
        );

        query.addCriteria(criteria);
        log.info(query.toString());

        List<Friends> friendsList = mongoTemplate.find(query, Friends.class);

        return friendsList.stream()
                .map(friendship -> {
                    if (userId.equals(friendship.getUser1())) {
                        return friendship.getUser2();
                    } else {
                        return friendship.getUser1();
                    }
                })
                .filter(friendId -> !friendId.equals(userId))
                .distinct()
                .collect(Collectors.toList());
    }



    @Override
    public void delete(String user1, String user2) {
        if(user1 == null || user2 == null || user1.equals(user2)){
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("user1").is(user1).and("user2").is(user2),
                Criteria.where("user1").is(user2).and("user2").is(user1)
        );
        query.addCriteria(criteria);

        Friends friends = mongoTemplate.findOne(query, Friends.class);
        if (friends == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        if(friends.getStatus() != Status.DELETED) {
            friends.setStatus(Status.DELETED);
            friendsRepository.save(friends);
        }
    }

    @Override
    public List<String> findMutualFriends(String userId1, String userId2) {
        if (userId1 == null || userId2 == null || userId1.equals(userId2)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        List<String> friends1 = findFriendIdByUserId(userId1);
        List<String> friends2 = findFriendIdByUserId(userId2);

        return friends1.stream()
                .filter(friends2::contains)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> countMutualFriendsForMultipleUsers(String currentUserId, List<String> targetUserIds) {
        if (currentUserId == null || targetUserIds == null || targetUserIds.isEmpty()) {
            return new HashMap<>();
        }

        List<String> currentUserFriends = findFriendIdByUserId(currentUserId);
        Map<String, List<String>> allTargetUsersFriends = new HashMap<>();

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("status").is(Status.ACTIVE),
                new Criteria().orOperator(
                        Criteria.where("user1").in(targetUserIds),
                        Criteria.where("user2").in(targetUserIds)
                )
        );
        query.addCriteria(criteria);

        List<Friends> allFriendships = mongoTemplate.find(query, Friends.class);

        for (String targetUserId : targetUserIds) {
            List<String> targetUserFriends = allFriendships.stream()
                    .filter(f -> targetUserId.equals(f.getUser1()) || targetUserId.equals(f.getUser2()))
                    .map(f -> targetUserId.equals(f.getUser1()) ? f.getUser2() : f.getUser1())
                    .collect(Collectors.toList());
            allTargetUsersFriends.put(targetUserId, targetUserFriends);
        }

        Map<String, Integer> result = new HashMap<>();
        for (String targetUserId : targetUserIds) {
            List<String> targetUserFriends = allTargetUsersFriends.get(targetUserId);
            if (targetUserFriends != null) {
                long mutualCount = currentUserFriends.stream()
                        .filter(targetUserFriends::contains)
                        .filter(friendId -> !friendId.equals(targetUserId))
                        .count();
                result.put(targetUserId, (int) mutualCount);
            } else {
                result.put(targetUserId, 0);
            }
        }

        return result;
    }

    @Override
    public Map<String, List<String>> findMutualFriendsForMultipleUsers(String currentUserId, List<String> targetUserIds) {
        if (currentUserId == null || targetUserIds == null || targetUserIds.isEmpty()) {
            return new HashMap<>();
        }

        List<String> currentUserFriends = findFriendIdByUserId(currentUserId);

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("status").is(Status.ACTIVE),
                new Criteria().orOperator(
                        Criteria.where("user1").in(targetUserIds),
                        Criteria.where("user2").in(targetUserIds)
                )
        );
        query.addCriteria(criteria);

        List<Friends> allFriendships = mongoTemplate.find(query, Friends.class);

        Map<String, List<String>> result = new HashMap<>();
        for (String targetUserId : targetUserIds) {
            List<String> targetUserFriends = allFriendships.stream()
                    .filter(f -> targetUserId.equals(f.getUser1()) || targetUserId.equals(f.getUser2()))
                    .map(f -> targetUserId.equals(f.getUser1()) ? f.getUser2() : f.getUser1())
                    .collect(Collectors.toList());

            List<String> mutualFriends = currentUserFriends.stream()
                    .filter(targetUserFriends::contains)
                    .filter(friendId -> !friendId.equals(targetUserId))
                    .collect(Collectors.toList());

            result.put(targetUserId, mutualFriends);
        }

        return result;
    }

    public List<FriendSuggestionResponse> suggestFriends(FriendSuggestionRequest request) {
        User currentUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get current user's friends
        List<String> currentFriendIds = findFriendIdByUserId(request.getUserId());

        // Get all users except current user and existing friends
        List<User> potentialFriends = userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(request.getUserId()))
                .filter(user -> !currentFriendIds.contains(user.getId()))
                .collect(Collectors.toList());

        List<FriendSuggestionResponse> suggestions = new ArrayList<>();

        for (User potentialFriend : potentialFriends) {
            try {
                // Calculate mutual friends
                List<User> mutualFriends = calculateMutualFriends(currentUser.getId(), potentialFriend.getId());

                // Calculate distance
                double distance = calculateDistance(currentUser.getAddress(), potentialFriend.getAddress());

                // Skip if distance is too far
                if (distance > request.getMaxDistance()) {
                    continue;
                }

                // Get location strings
                String currentLocation = getLocationString(currentUser.getAddress());
                String suggestionLocation = getLocationString(potentialFriend.getAddress());

                // Get AI-powered suggestion score and reason
                Map<String, Object> aiResult = openAIService.generateFriendSuggestionScore(
                        mutualFriends.size(), distance, currentLocation, suggestionLocation
                );

                double score = (Double) aiResult.get("score");
                String reason = (String) aiResult.get("reason");

                FriendSuggestionResponse suggestion = FriendSuggestionResponse.fromUser(
                        potentialFriend,
                        mutualFriends.size(),
                        mutualFriends,
                        distance,
                        suggestionLocation,
                        score,
                        reason
                );

                suggestions.add(suggestion);
            } catch (Exception e) {
                log.error("Error processing friend suggestion for user {}", potentialFriend.getId(), e);
            }
        }

        // Sort by AI score (descending) and limit results
        return suggestions.stream()
                .sorted((a, b) -> Double.compare(b.getSuggestionScore(), a.getSuggestionScore()))
                .limit(request.getLimit())
                .collect(Collectors.toList());
    }

    public List<User> calculateMutualFriends(String userId1, String userId2) {
        List<String> friends1 = findFriendIdByUserId(userId1);
        List<String> friends2 = findFriendIdByUserId(userId2);

        List<String> mutualFriendIds = friends1.stream()
                .filter(friends2::contains)
                .collect(Collectors.toList());

        return userRepository.findAllById(mutualFriendIds);
    }

    public double calculateDistance(Address address1, Address address2) {
        if (address1 == null || address2 == null) {
            return Double.MAX_VALUE;
        }

        return calculateDistanceKm(
                address1.getLatitude(), address1.getLongitude(),
                address2.getLatitude(), address2.getLongitude()
        );
    }

    public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public String getLocationString(Address address) {
        if (address == null) {
            return "Unknown location";
        }

        StringBuilder location = new StringBuilder();
        if (address.getCommune() != null) location.append(address.getCommune()).append(", ");
        if (address.getDistrict() != null) location.append(address.getDistrict()).append(", ");
        if (address.getProvince() != null) location.append(address.getProvince()).append(", ");
        if (address.getCountry() != null) location.append(address.getCountry());

        return location.toString().replaceAll(", $", "");
    }

}
