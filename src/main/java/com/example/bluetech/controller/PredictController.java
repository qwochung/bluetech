package com.example.bluetech.controller;

import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.Predict;
import com.example.bluetech.service.PredictService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(value = "/predict")
public class PredictController {
    PredictService predictService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response predict(@RequestBody Predict predict) throws BadRequestException {
        return Response.builder(predictService.create(predict)).build();
    }


}
