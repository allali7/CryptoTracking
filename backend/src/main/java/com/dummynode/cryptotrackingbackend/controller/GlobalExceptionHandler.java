/**
 * HuaiHao Mo
 */
package com.dummynode.cryptotrackingbackend.controller;

import com.dummynode.cryptotrackingbackend.service.ApiResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        return new ApiResponse<>(500, "Internal Server Error", null, ex.getMessage());
    }
}
