package com.example.bluetech.controller;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.dto.Response;
import com.example.bluetech.entity.Post;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;



    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response post(@RequestBody Post post) {
        Post p = postService.add(post);
        return  Response.builder(p).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response get(@PathVariable String id) {
        Post post = postService.findById(id).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(post).build();

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response getAll() {
        List<Post> posts = postService.findAll();
        return Response.builder(posts).build();
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
}
