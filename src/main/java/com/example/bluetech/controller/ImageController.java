package com.example.bluetech.controller;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.Response;
import com.example.bluetech.entity.Image;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.service.ImageService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/image")
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private ListableBeanFactory listableBeanFactory;

    @RequestMapping(value = "/upload", method =  RequestMethod.POST)
    public Response uploadImage(@RequestParam("file") MultipartFile file) {
        Image image = imageService.add(file);
        return Response.builder(image).build();
    }


    @RequestMapping(value = "/upload/multi", method =  RequestMethod.POST)
    public Response uploadImage(@RequestParam("files") MultipartFile[] files) {
        List<Image> images = imageService.add(files);
        return Response.builder(images).build();
    }



    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response findById(@PathVariable String id) {
        Image image = imageService.findActiveIamgeById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(image).build();
    }


    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response deleteById(@PathVariable String id) {
        imageService.deleteById(id);
        return Response.builder("Success").build();
    }

}
