package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.constant.Status;
import com.example.bluetech.entity.Post;
import com.example.bluetech.entity.Reaction;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.CommentRepository;
import com.example.bluetech.repository.PostRepository;
import com.example.bluetech.repository.ReactionCountProjection;
import com.example.bluetech.repository.ReactionRepository;
import com.example.bluetech.service.PostService;
import com.example.bluetech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post add(Post post) {
        if (post.getOwner() == null || post.getOwnerType() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (post.getTextContent().isEmpty() && post.getImage().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        User user = userService.findById(post.getOwner().getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        post.setCreatedAt(System.currentTimeMillis());
        return postRepository.save(post);
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return posts.stream()
                .map(this::enrichPostWithCounts)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> getFeed(int page, int size, String orderBy, Sort.Direction direction) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(Status.ACTIVE));
        query.with(Sort.by(direction, orderBy));
        query.skip((long) page * size).limit(size);

        List<Post> posts = mongoTemplate.find(query, Post.class);
        return posts.stream()
                .map(this::enrichPostWithCounts)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> findById(String id, String userId) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            enrichPostWithCounts(post);
            if (userId != null) {
                enrichPostWithUserReaction(post, userId);
            }
            return Optional.of(post);
        }
        return Optional.empty();
    }

    @Override
    public List<Post> findByOwnerIdAndOwnerType(String ownerId, OwnerType ownerType) {
        if (ownerType == OwnerType.PERSON) {
            User user = userService.findById(ownerId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }
        List<Post> posts = postRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
        return posts.stream()
                .map(this::enrichPostWithCounts)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByTag(String tag) {
        return List.of();
    }

    @Override
    public Post update(String id, Post post) {
        Post postToUpdate = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        postToUpdate = postToUpdate.update(post);
        return postRepository.save(postToUpdate);
    }

    @Override
    public void delete(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        post.setStatus(Status.DELETED);
        post.setDeletedAt(System.currentTimeMillis());
        postRepository.save(post);
    }

    /**
     * Enrich post with reaction and comment counts
     */
    private Post enrichPostWithCounts(Post post) {
        // Set reaction count
        long reactionCount = reactionRepository.countByPostId(post.getId());
        post.setNoOfReactions((int) reactionCount);

        // Set comment count
        long commentCount = commentRepository.findByPostId(post.getId()).size();
        post.setNoOfComments((int) commentCount);

        // Set reaction counts by type - DIRECT implementation to avoid circular dependency
        Map<ReactionType, Integer> reactionCounts = getReactionCounts(post.getId());
        post.setReactionCounts(reactionCounts);

        return post;
    }

    /**
     * Enrich post with user-specific reaction info
     */
    private Post enrichPostWithUserReaction(Post post, String userId) {
        Optional<Reaction> userReaction = reactionRepository.findByPostIdAndUserId(post.getId(), userId);
        if (userReaction.isPresent()) {
            post.setIsReacted(true);
            post.setUserReactionType(userReaction.get().getReactionType());
        } else {
            post.setIsReacted(false);
            post.setUserReactionType(null);
        }
        return post;
    }

    /**
     * Public method to enrich posts with user reaction info
     */
    public List<Post> enrichPostsWithUserReaction(List<Post> posts, String userId) {
        if (userId == null) return posts;

        return posts.stream()
                .map(post -> enrichPostWithUserReaction(post, userId))
                .collect(Collectors.toList());
    }

    /**
     * Get reaction counts by type - moved from ReactionService to avoid circular dependency
     */
    private Map<ReactionType, Integer> getReactionCounts(String postId) {
        List<ReactionCountProjection> counts = reactionRepository.countReactionsByPostId(postId);
        Map<ReactionType, Integer> reactionCounts = new HashMap<>();

        for (ReactionCountProjection count : counts) {
            reactionCounts.put(count.getReactionType(), count.getCount());
        }

        return reactionCounts;
    }
}