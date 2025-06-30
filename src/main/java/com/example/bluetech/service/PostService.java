package com.example.bluetech.service;

import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.entity.Post;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post save(Post post);
    Post add(Post post);
    List<Post> findAll();
    Optional<Post> findById(String id);

    List<Post> getFeed(int page, int size, String orderBy, Sort.Direction direction);

    Optional<Post> findByIdAndUserId(String id, String userId);

    List<Post> findByOwnerIdAndOwnerType(String ownerId, OwnerType ownerType);
    List<Post> findByTag(String tag);
    Post update(String id,Post post);
    void delete(String id);

}
