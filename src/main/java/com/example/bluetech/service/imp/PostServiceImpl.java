package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.constant.Status;
import com.example.bluetech.entity.Post;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.PostRepository;
import com.example.bluetech.service.PostService;
import com.example.bluetech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl  implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;


    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post add(Post post) {
        if (post.getOwnerId().isEmpty() || post.getOwnerType() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (post.getTextContent().isEmpty() && post.getImage().isEmpty() ){
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        User user = userService.findById(post.getOwnerId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return this.save(post);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> findById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findByOwnerIdAndOwnerType(String ownerId, OwnerType ownerType) {
        if (ownerType == OwnerType.PERSON){
            User user = userService.findById(ownerId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }
        return postRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    @Override
    public List<Post> findByTag(String tag) {
        return List.of();
    }

    @Override
    public Post update(String id, Post post) {
        Post postToUpdate = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        postToUpdate =  postToUpdate.update(post);
        return postRepository.save(postToUpdate);
    }

    @Override
    public void delete(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        post.setStatus(Status.DELETED);
        post.setDeletedAt(System.currentTimeMillis());
        postRepository.save(post);


    }
}
