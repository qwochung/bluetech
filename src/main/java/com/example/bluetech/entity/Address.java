package com.example.bluetech.entity;

import com.example.bluetech.dto.IpLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("address")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address extends BaseEntity {
        String country;
    String province;
    String district;
    String commune;
    String detail;
    double latitude;
    double longitude;


    public static Address createByIp(IpLocationResponse response) throws JsonProcessingException {
        Address address = new Address();
        address.setCountry(response.getCountry());
        address.setProvince(response.getRegion());
        address.setLatitude(response.getLatLong()[0]);
        address.setLongitude(response.getLatLong()[1]);

        return address;
    }
}
