package com.example.demo.exception;

import lombok.Getter;

@Getter                          //chứa lỗi
public class AppException extends RuntimeException{
    public  AppException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode=errorCode;
    }
    private ErrorCode errorCode;
}
