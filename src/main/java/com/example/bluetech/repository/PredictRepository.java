package com.example.bluetech.repository;

import com.example.bluetech.constant.ReferencesType;
import com.example.bluetech.entity.Predict;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PredictRepository extends MongoRepository<Predict, String> {
    Optional<Predict> findByReferencesTypeAndReferencesIdIn(ReferencesType referencesType, Collection<String> referencesIds);
}
