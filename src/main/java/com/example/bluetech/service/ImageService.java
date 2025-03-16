package com.example.bluetech.service;

import com.example.bluetech.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ImageService {

    Image save(Image image);
    Image add(MultipartFile file);
    List<Image> add(MultipartFile[] files );
    Optional<Image> findById(String id);
    List<Image> findAll();
    Image update(Image image);
    void deleteById(String id);
}
