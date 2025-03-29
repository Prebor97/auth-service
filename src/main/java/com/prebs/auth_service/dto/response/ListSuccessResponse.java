package com.prebs.auth_service.dto.response;

import java.util.List;

public class ListSuccessResponse <T>{
    public int status;
    public String message;
    public List<T> data;

    public ListSuccessResponse(int status, String message, List<T> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
