package com.dummynode.cryptotrackingbackend.exception;

public class OrderProcessingException extends BusinessException {

    private static final String ERROR_CODE = "ORDER_PROCESSING_ERROR";

    public OrderProcessingException(String message) {
        super(ERROR_CODE, message);
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
