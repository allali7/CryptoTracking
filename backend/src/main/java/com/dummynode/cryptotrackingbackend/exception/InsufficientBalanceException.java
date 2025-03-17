package com.dummynode.cryptotrackingbackend.exception;

public class InsufficientBalanceException extends BusinessException {

    private static final String ERROR_CODE = "INSUFFICIENT_BALANCE";

    public InsufficientBalanceException(String message) {
        super(ERROR_CODE, message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
