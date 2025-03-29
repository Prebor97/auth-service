package com.prebs.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for logging user in")
public class NoDataSuccessResponse {
    @Schema(description = "success status code", example = "200")
    public int status;
    @Schema(description = "success response message", example = "User deleted successfully")
    public String message;

    public NoDataSuccessResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
