package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.Status;
import com.example.bluetech.entity.Comment;
import com.example.bluetech.entity.Post;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.CommentRepository;
import com.example.bluetech.service.CommentService;
import com.example.bluetech.service.PostService;
import com.example.bluetech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment) ;
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        Comment comment = commentRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        comment.setStatus(Status.DELETED);
        comment.setDeletedAt(System.currentTimeMillis());
        commentRepository.save(comment);
    }

    @Override
    public Comment update(Comment comment, String id) {
        Comment commentToUpdate = commentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        commentToUpdate =  commentToUpdate.update(comment);
        return commentRepository.save(commentToUpdate);
    }

    @Override
    public List<Comment> findByPostId(String postId) {
        Post post = postService.findById(postId, null).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        return commentRepository.findByPostId(postId);
    }


    @Override
    public List<Comment> findByParent(String CommentParentId) {
        Comment parent = commentRepository.findById(CommentParentId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if(parent.getStatus() == Status.DELETED){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return commentRepository.findByParentId(CommentParentId);
    }

    @Override
    public Comment add(Comment comment) {
        if (comment.getOwner().getId().isEmpty() || comment.getTextContent().isEmpty() || comment.getPostId().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        User user = userService.findById(comment.getOwner().getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        comment.setCreatedAt(System.currentTimeMillis());
        return commentRepository.save(comment);
    }
}
