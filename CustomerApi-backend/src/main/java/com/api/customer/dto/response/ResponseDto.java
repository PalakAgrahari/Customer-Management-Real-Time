package com.api.customer.dto.response;

import lombok.Data;

@Data
public class ResponseDto {
    private Boolean status;
    private Object data;
    private String message;
}
