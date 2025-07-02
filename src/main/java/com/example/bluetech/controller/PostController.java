package com.example.bluetech.controller;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.Post;
import com.example.bluetech.entity.Reaction;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.ReactionRepository;
import com.example.bluetech.service.PostService;
import com.example.bluetech.service.imp.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostServiceImpl postServiceImpl;

    @Autowired
    private ReactionRepository reactionRepository;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response createPost(@RequestBody Post post) {
        Post p = postService.add(post);
        return  Response.builder(p).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response get(@PathVariable String id, @RequestParam(required = false) String userId) {
        Post post = postService.findByIdAndUserId(id, userId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(post).build();

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response getAll(@RequestParam(required = false) String userId) {
        List<Post> posts = postService.findAll();

        // Enrich với thông tin reaction của user nếu có userId
        if (userId != null) {
            posts = postServiceImpl.enrichPostsWithUserReaction(posts, userId);
        }

        return Response.builder(posts).build();
    }

    @RequestMapping(value = "/feed", method = RequestMethod.GET)
    public Response feed(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
                @RequestParam(required = false) String userId
    ) {
        List<Post> posts = postService.getFeed(page, size, orderBy, direction);
        if (userId != null) {
            posts = postServiceImpl.enrichPostsWithUserReaction(posts, userId);
        }
        return Response.builder(posts).build();
    }

    @RequestMapping(value = "/owner/{ownerId}", method = RequestMethod.GET)
    public Response getByOwner(
            @PathVariable("ownerId") String ownerId,
            @RequestParam(defaultValue = "PERSON") OwnerType type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false) String userId
    ) {
        List<Post> posts = postService.findByOwnerIdAndOwnerType(ownerId, type, page, size, orderBy, direction);


        if (userId != null) {
            posts = postServiceImpl.enrichPostsWithUserReaction(posts, userId);
        }

        return Response.builder(posts).build();
    }

    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
    public Response update(@PathVariable("id") String id, @RequestBody Post post) {
        Post p = postService.update(id, post);
        return Response.builder(p).build();
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response delete(@PathVariable("id") String id) {
        postService.delete(id);
        return Response.builder("Success").build();
    }

    @RequestMapping(value = "/{postId}/reaction", method = RequestMethod.GET)
    public Response getUserReaction(@PathVariable String postId, @RequestParam String userId) {
        Optional<Reaction> reaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        return Response.builder(reaction.orElse(null)).build();
    }

    @RequestMapping(value = "/{postId}/reactions", method = RequestMethod.GET)
    public Response getPostReactions(@PathVariable String postId) {
        List<Reaction> reactions = reactionRepository.findByPostId(postId);

        Map<ReactionType, List<Map<String, Object>>> reactionsByType = reactions.stream()
                .collect(Collectors.groupingBy(
                        Reaction::getReactionType,
                        Collectors.mapping(reaction -> {
                            Map<String, Object> reactionInfo = new HashMap<>();
                            reactionInfo.put("userId", reaction.getUserId());
                            reactionInfo.put("createdAt", reaction.getCreatedAt());
                            return reactionInfo;
                        }, Collectors.toList())
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("reactionsByType", reactionsByType);
        result.put("totalReactions", reactions.size());

        return Response.builder(result).build();
    }
}