package com.example.bluetech.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;


    @Value("${aws.s3.bucket-name}")
    private String bucketName;


    public String uploadFile( MultipartFile file) throws IOException {

        String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
//                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes() )
        );


        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
}
