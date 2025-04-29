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

import java.util.List;
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

        criteria.orOperator(
                Criteria.where("user1").is(userId),
                Criteria.where("user2").is(userId)
        );
        query.addCriteria(Criteria.where("status").is(Status.ACTIVE));
        log.info(query.toString());

        List<Friends > friendsList = mongoTemplate.find(query, Friends.class);

        return friendsList.stream().map(f-> {
            String user1 = f.getUser1();
            String user2 = f.getUser2();
            return userId.equals(user1) ? user2 : user1;
        }).toList();

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


}
