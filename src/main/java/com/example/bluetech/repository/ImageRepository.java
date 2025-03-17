package com.example.bluetech.repository;

import com.example.bluetech.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

    @Query(value = "{'id' : ?0, 'status': {$ne:'DELETED'}}")
    Optional<Image> findActiveImageById(String id);
}
