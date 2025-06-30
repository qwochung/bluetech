package com.example.bluetech.service;

import com.example.bluetech.constant.ReferencesType;
import com.example.bluetech.entity.Predict;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

public interface PredictService {
    Predict save(Predict predict);
    Predict create (Predict predict) throws BadRequestException;
    Predict update (Predict predict);

    Optional<Predict> findById(String id);
    List<Predict> findAll();
    List<Predict> findByReferencesTypeAndReferenceIdIn(ReferencesType referencesType, List<String> referenceId);

}
