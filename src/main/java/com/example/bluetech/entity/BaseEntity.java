package com.example.bluetech.entity;


import com.example.bluetech.constant.Status;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
public class BaseEntity {

    @Id
    private String id;
    private Long createdAt ;
    private Long deletedAt;
    private Long updatedAt;
    @Setter
    private Status status = Status.ACTIVE;


}
