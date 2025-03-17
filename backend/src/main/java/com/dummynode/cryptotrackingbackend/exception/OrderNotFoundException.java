package com.dummynode.cryptotrackingbackend.exception;

public class OrderNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "ORDER_NOT_FOUND";

    public OrderNotFoundException(String message) {
        super(ERROR_CODE, message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
