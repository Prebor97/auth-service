package com.prebs.auth_service.exception;

public class UserNotActivatedException extends Exception{
    public UserNotActivatedException(String message){
        super(message);
    }
}
