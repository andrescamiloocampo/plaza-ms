package com.pragma.powerup.infrastructure.exceptionhandler;

public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    INVALID_NAME("The provided name is not valid"),
    INVALID_NIT("The provided NIT is not valid"),
    INVALID_PHONE("The provided phone number is not valid"),
    INVALID_ROLE("The specified role is invalid"),
    DOMAIN_EXCEPTION("A domain rule was violated"),
    USER_NOT_FOUND("User not found");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
