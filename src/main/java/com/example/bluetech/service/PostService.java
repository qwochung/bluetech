package com.example.bluetech.service;

import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post save(Post post);
    Post add(Post post);
    List<Post> findAll();
//    Optional<Post> findById(String id);

    Optional<Post> findById(String id, String userId);

    List<Post> findByOwnerIdAndOwnerType(String ownerId, OwnerType ownerType);
    List<Post> findByTag(String tag);
    Post update(String id,Post post);
    void delete(String id);
}
