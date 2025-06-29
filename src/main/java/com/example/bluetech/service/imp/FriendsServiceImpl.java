package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.Status;
import com.example.bluetech.entity.Friends;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.FriendsRepository;
import com.example.bluetech.service.FriendsService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendsServiceImpl implements FriendsService {
    @Autowired
    FriendsRepository friendsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

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
}
