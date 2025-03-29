package com.prebs.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for logging user in")
public class SuccessResponseDto<T> {
    @Schema(description = "success status code", example = "201")
    public int status;
    @Schema(description = "description of response message", example = "User logged in successfully")
    public String message;
    @Schema(description = "data to be returned", example = "{\"id\":\"qtq\",\"name\":\"prebs\",\"email\":\"prebs@yahoo.com\"}")
    public T data;

    public SuccessResponseDto() {
    }

    public SuccessResponseDto(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
