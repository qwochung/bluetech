package com.example.bluetech.controller;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.Response;
import com.example.bluetech.entity.Comment;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Response get(@PathVariable String id ){
        Comment comment = commentService.findById(id).orElseThrow(()->  new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(comment).build();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response post(@RequestBody @Valid Comment comment){
        Comment p = commentService.add(comment);
        return  Response.builder(p).build();
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response getAll() {
        List<Comment> comments = commentService.findAll();
        return Response.builder(comments).build();
    }

    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
    public Response update(@PathVariable("id") String id,  @RequestBody Comment comment) {
        Comment c = commentService.update( comment,id);
        return Response.builder(c).build();
    }


    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response delete(@PathVariable("id") String id) {
        commentService.delete(id);
        return Response.builder("Success").build();
    }

    @RequestMapping(value = "/post/{postId}", method = RequestMethod.GET)
    public Response getByPost(@PathVariable String postId) {
        List<Comment> comments = commentService.findByPostId(postId);
        return Response.builder(comments).build();
    }


}
