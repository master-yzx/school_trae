package com.campus.common.result;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(ResultCode.SUCCESS.getCode());
        resp.setMessage(ResultCode.SUCCESS.getMessage());
        resp.setData(data);
        return resp;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> failure(ResultCode code) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(code.getCode());
        resp.setMessage(code.getMessage());
        return resp;
    }

    public static <T> ApiResponse<T> failure(ResultCode code, String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(code.getCode());
        resp.setMessage(message);
        return resp;
    }
}

