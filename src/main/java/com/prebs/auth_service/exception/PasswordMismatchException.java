package com.prebs.auth_service.exception;

public class PasswordMismatchException  extends Exception{
    public PasswordMismatchException(String message){
        super(message);
    }
}

