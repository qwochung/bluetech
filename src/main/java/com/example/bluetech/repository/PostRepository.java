package com.example.bluetech.repository;

import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findByOwnerIdAndOwnerType(String ownerId, OwnerType ownerType);
}
