package com.campus.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(0, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    SERVER_ERROR(500, "Internal Server Error"),

    BUSINESS_ERROR(1000, "Business Error"),
    VALIDATION_ERROR(1001, "Validation Error");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

