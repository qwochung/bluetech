package com.example.bluetech.entity;

import com.example.bluetech.constant.ReferencesType;
import com.example.bluetech.constant.ViolationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Predict extends BaseEntity {

    String referencesId;
    ReferencesType referencesType;
    ViolationType violationType;
    Boolean violationDetected;
    double confidence;
    Map<String, Object> metadata;

    public  Predict convert( Predict predict ,Object data ) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        predict = objectMapper.convertValue(data, Predict.class);
        return predict;
    }

}
