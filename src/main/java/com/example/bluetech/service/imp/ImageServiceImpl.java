package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.Status;
import com.example.bluetech.entity.Image;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.ImageRepository;
import com.example.bluetech.service.ImageService;
import com.example.bluetech.service.ThirdParty.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ImageServiceImpl implements ImageService {

    private static int maxFileSize = 5;

    private static final long MAX_FILE_SIZE = (long) maxFileSize * 1024 * 1024;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/png", "image/jpeg", "image/jpg, image/*");



    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private S3Service s3Service;



    @Override
    public Image save(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image add(MultipartFile file) {

        String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
        }


        if ( file.getContentType() == null || !ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new AppException(ErrorCode.FILE_EXTENSION_NOT_SUPPORTED);
        }

        try {
            String url = s3Service.uploadFile(file, key);
            Image image = Image.create(url,key);
            return imageRepository.save(image);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public List<Image> add(MultipartFile[] files) {
        List<Image> images = new ArrayList<>();

        Arrays.stream(files).forEach(file -> {

            String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
            }


            if ( file.getContentType() == null || !ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                throw new AppException(ErrorCode.FILE_EXTENSION_NOT_SUPPORTED);
            }

            try {
                String url = s3Service.uploadFile(file, key);
                Image image = Image.create(url,key);
                imageRepository.save(image);
                images.add(image);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return images;
    }

    @Override
    public Optional<Image> findActiveIamgeById(String id) {
        return imageRepository.findActiveImageById(id);
    }

    @Override
    public List<Image> findAll() {
        return List.of();
    }

    @Override
    public Image update(Image image) {
        return null;
    }

    @Override
    public void deleteById(String id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            image.setStatus(Status.DELETED);
            imageRepository.save(image);
        }
    }
}
