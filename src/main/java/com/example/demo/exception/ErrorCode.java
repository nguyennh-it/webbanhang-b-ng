package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NOT_EXISTED(1001, "Sản phẩm không tồn tại",HttpStatus.NOT_FOUND),
    PRODUCT_EXISTED(1002, "Sản phẩm đã tồn tại",HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_REQUIRED(1003, "Tên sản phẩm không được để trống",HttpStatus.BAD_REQUEST),
    INVALID_PRICE(1004, "Giá sản phẩm phải từ 0 trở lên",HttpStatus.BAD_REQUEST),
    USER_EXISTED(1005, "Người dùng đã tồn tại",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1006,"Mật khẩu phải có 8 ký tự trở lên",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1007, "Người dùng phải có 3 ký tự trở lên",HttpStatus.BAD_REQUEST ),
    USER_NOT_EXISTED(1005, "Người dùng không  tồn tại",HttpStatus.BAD_REQUEST)
    ;


    ErrorCode(int code, String message,HttpStatusCode statusCode) {
        this.code=code;
        this.message=message;
        this.statusCode = statusCode;
    }
    private int code;
    private String message;
    private final HttpStatusCode statusCode;

}

