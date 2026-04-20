package com.example.timetracker.exception.user;

public class UserServiceException extends RuntimeException {
    public UserServiceException(String message){
        super(message);
    }
}
