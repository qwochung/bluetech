package com.example.bluetech.repository;

import com.example.bluetech.entity.Friends;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendsRepository extends MongoRepository<Friends, String> {
}
