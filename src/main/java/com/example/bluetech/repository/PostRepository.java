package com.example.bluetech.repository;

import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByOwnerIdAndOwnerType(
            String ownerId,
            OwnerType ownerType,
            Pageable pageable
    );
}
