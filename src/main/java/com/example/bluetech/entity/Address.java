package com.example.bluetech.entity;

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

}
