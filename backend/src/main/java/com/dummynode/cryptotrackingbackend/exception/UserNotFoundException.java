package com.dummynode.cryptotrackingbackend.exception;

public class UserNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "USER_NOT_FOUND";

    public UserNotFoundException(String message) {
        super(ERROR_CODE, message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
