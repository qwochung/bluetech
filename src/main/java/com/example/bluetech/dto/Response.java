package com.example.bluetech.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;


@Data

public class Response {

    private Integer code;
    private String status;
    private String message;
    private Object data;


    public static Builder builder(Object data){
        Builder builder = new   Builder();
        builder.setData(data);
        return  builder;
    }

    public static class Builder{
        private Integer code;
        private String status;
        private String message;
        private Object data;


        void setData(Object data) {
            this.data = data;
        }


        public Response build() {
            Response response = new Response();

            if(this.code == null){
                this.code = 200;
            }
            response.setCode(code);

            if(this.status == null){
                this.status = HttpStatus.OK.getReasonPhrase();
            }
            response.setStatus(status);


            if(this.message != null){
                response.setMessage(message);
            }
            else {
                response.setMessage("");
            }

            if(this.data != null){
                response.setData(data);
            }
            else {
                response.setData(null);
            }

            return response;
        }





    }


}
