package com.prebs.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for logging user in")
public class ErrorResponseDto {
    @Schema(description = "error status code", example = "404")
    public int status;
    @Schema(description = "error description", example = "Using not found")
    public String message;
    @Schema(description = "details of api that caused error", example = "/api/auth/login/**")
    public String details;


    public ErrorResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
           }

    public ErrorResponseDto(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
