package entity;


import constant.Status;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
public class BaseEntity {

    @Id
    private String id;

    private Long createdDate;
    private Long deletedDate;

    @Setter
    private Status status;

}
