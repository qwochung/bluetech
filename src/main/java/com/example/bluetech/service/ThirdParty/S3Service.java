package com.example.bluetech.service.ThirdParty;


import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;


    @Value("${aws.s3.bucket-name}")
    private String bucketName;




    public String uploadFile( MultipartFile file, String key) throws IOException, AppException {

        if (key == null || key.isEmpty()) {
             key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        }
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                         .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes() )
        );


        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }


    public String deleteFile(String key) throws AppException {
        if (key == null || key.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        s3Client.deleteObject(
                DeleteObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );

        return "Deleted " + key;
    }
}
