package com.example.bluetech.service.imp;

import com.example.bluetech.config.RabbitMQConfig;
import com.example.bluetech.constant.*;
import com.example.bluetech.entity.*;
import com.example.bluetech.messaging.message.PredictMessage;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.messaging.producer.PredictProducer;
import com.example.bluetech.repository.CommentRepository;
import com.example.bluetech.repository.PostRepository;
import com.example.bluetech.repository.ReactionCountProjection;
import com.example.bluetech.repository.ReactionRepository;
import com.example.bluetech.service.PostService;
import com.example.bluetech.service.PredictService;
import com.example.bluetech.service.UserService;
import com.example.bluetech.service.client.PredictClientService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostServiceImpl implements PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserService userService;

    @Autowired
    ReactionRepository reactionRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PredictService predictService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PredictProducer predictProducer;




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
        Post savedPost = postRepository.save(post);

        predictProducer.addToMessagesQueue(savedPost);
        return  savedPost;
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return posts.stream()
                .map(this::enrichPostWithCounts)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> findById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> getFeed(int page, int size, String orderBy, Sort.Direction direction) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(Status.ACTIVE));
        query.with(Sort.by(direction, orderBy));
        query.skip((long) page * size).limit(size);

        List<Post> posts = mongoTemplate.find(query, Post.class);

        posts = violenceDetected(posts);

        return posts.stream()
                .map(this::enrichPostWithCounts)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> findByIdAndUserId(String id, String userId) {
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
    public List<Post> findByOwnerIdAndOwnerType(String ownerId, OwnerType ownerType, int page, int size, String orderBy, Sort.Direction direction) {
        if (ownerType == OwnerType.PERSON) {
            userService.findById(ownerId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        Page<Post> postPage = postRepository.findByOwnerIdAndOwnerType(ownerId, ownerType, (java.awt.print.Pageable) pageable);

        return postPage.getContent()
                .stream()
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


    private List<Post> violenceDetected(List<Post> posts) {
        for (Post post : posts) {
            Predict predictContent = predictService.findByReferencesTypeAndReferenceIdAndViolationDetected(ReferencesType.POST, post.getId(), true)
                    .orElse(null);

            if (predictContent != null && predictContent.getViolationDetected() ) {
                post.setViolationDetected(true);
            }
            else {
                List<Image> images = post.getImage();
                for (Image image : images) {
                    Predict predictImage = predictService.findByReferencesTypeAndReferenceIdAndViolationDetected(
                            ReferencesType.IMAGE,
                            image.getId(),
                            true
                    ).orElse(null);
                    if (predictImage != null && predictImage.getViolationDetected()) {
                        post.setViolationDetected(true);
                    }
                                        else {
                        post.setViolationDetected(false);
                    }
                }

            }
        }

        return posts;
    }


}