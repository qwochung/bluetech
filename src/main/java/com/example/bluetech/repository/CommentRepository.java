package com.example.bluetech.repository;

import com.example.bluetech.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    List<Comment> findByOwnerId(String ownerId);
    List<Comment> findByParentId(String parentId);
}
