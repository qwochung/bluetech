package com.example.bluetech.controller;


import com.example.bluetech.dto.Response;
import com.example.bluetech.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/s3")
public class UploadController {

    @Autowired
    private S3Service s3Service;


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Response upload(@RequestParam("files") MultipartFile[] files) {
        List<String> fileUrls = new ArrayList<>();
        try{
            for (MultipartFile file : files) {
                String url = s3Service.uploadFile(file);
                fileUrls.add(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return Response.builder(fileUrls).build();
    }

}
