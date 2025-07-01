package com.example.bluetech.messaging.message;

import com.example.bluetech.constant.ReferencesType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PredictMessage implements Serializable {
    String referencesId;
    ReferencesType referencesType;
    String content;
    String url;
    String comment;
}
