package com.example.bluetech.controller;


import com.example.bluetech.dto.Response;
import com.example.bluetech.service.ThirdParty.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Response upload(@RequestParam("files") MultipartFile[] files) {
        List<String> fileUrls = new ArrayList<>();
        try{
            for (MultipartFile file : files) {
                String url = s3Service.uploadFile(file, null);
                fileUrls.add(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return Response.builder(fileUrls).build();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Response delete(@RequestBody() String key) {

        return Response.builder(s3Service.deleteFile(key)).build();
    }

}
