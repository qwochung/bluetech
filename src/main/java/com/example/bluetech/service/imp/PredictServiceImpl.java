package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ReferencesType;
import com.example.bluetech.entity.Predict;
import com.example.bluetech.repository.PredictRepository;
import com.example.bluetech.service.PredictService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PredictServiceImpl implements PredictService {

    PredictRepository predictRepository;


    @Override
    public Predict save(Predict predict) {
        return predictRepository.save(predict);
    }

    @Override
    public Predict create(Predict predict) throws BadRequestException {
        if (predict.getReferencesId() == null || predict.getReferencesId().isEmpty()) {
            throw new BadRequestException("Missing information");
        }
        predict.setCreatedAt(System.currentTimeMillis());
        return predictRepository.save(predict);
    }

    @Override
    public Predict update(Predict predict) {
        return null;
    }

    @Override
    public Optional<Predict> findById(String id) {
        return predictRepository.findById(id);
    }

    @Override
    public List<Predict> findAll() {
        return predictRepository.findAll();
    }

    @Override
    public List<Predict> findByReferencesTypeAndReferenceIdIn(ReferencesType referencesType, List<String> referenceIds) {
        return predictRepository.findByReferencesTypeAndReferencesIdIn(referencesType, referenceIds);
    }
}
