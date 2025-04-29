package com.example.bluetech.controller;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.Post;
import com.example.bluetech.entity.Reaction;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.ReactionRepository;
import com.example.bluetech.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReactionRepository reactionRepository;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response createPost(@RequestBody Post post) {
        Post p = postService.add(post);
        return  Response.builder(p).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response get(@PathVariable String id, @RequestParam(required = false) String userId) {
        Post post = postService.findById(id, userId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(post).build();

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response getAll() {
        List<Post> posts = postService.findAll();
        return Response.builder(posts).build();
    }

    @RequestMapping(value = "/feed", method = RequestMethod.POST)
    public Response feed(
            @RequestParam() Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
            ) {
        return Response.builder(postService.getFeed(page, size, orderBy, direction)).build();
    }


    @RequestMapping(value = "/owner/{ownerId}")
    public Response getByOwner(@PathVariable("ownerId") String ownerId, @Param("type") OwnerType type) {
        List<Post> posts = postService.findByOwnerIdAndOwnerType(ownerId, type);
        return Response.builder(posts).build();
    }


    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
    public Response update(@PathVariable("id") String id,  @RequestBody Post post) {
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


}
