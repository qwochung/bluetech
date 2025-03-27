package com.example.bluetech.service;

import com.example.bluetech.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment save(Comment comment);
    List<Comment> findAll();
    Optional<Comment> findById(String id);
    void delete(String id);
    Comment update(Comment comment, String id);
    List<Comment> findByPost(String PostId);
    List<Comment> findByParent(String CommentParentId);
    Comment add(Comment comment);
}
