package com.example.bluetech.entity;


import com.example.bluetech.constant.Status;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
public class BaseEntity {

    @Id
    private String id;
    private Long createdAt;
    private Long deletedAt;
    private String createdBy;
    private String deletedBy;
    private String updatedBy;
    private String updatedAt;
    @Setter
    private Status status;


}
