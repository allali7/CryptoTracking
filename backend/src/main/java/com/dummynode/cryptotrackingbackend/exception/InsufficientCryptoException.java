package com.dummynode.cryptotrackingbackend.exception;

public class InsufficientCryptoException extends BusinessException {

    private static final String ERROR_CODE = "INSUFFICIENT_CRYPTO";

    public InsufficientCryptoException(String message) {
        super(ERROR_CODE, message);
    }

    public InsufficientCryptoException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
