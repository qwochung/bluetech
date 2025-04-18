package com.example.bluetech.repository;

import com.example.bluetech.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);

    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
}
