package com.pragma.powerup.infrastructure.exceptionhandler;

public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    INVALID_NAME("The provided name is not valid"),
    INVALID_NIT("The provided NIT is not valid"),
    INVALID_PHONE("The provided phone number is not valid"),
    INVALID_ROLE("The specified role is invalid"),
    DOMAIN_EXCEPTION("A domain rule was violated"),
    USER_NOT_FOUND("User not found"),
    RESTAURANT_NOT_FOUND("Restaurant not found"),
    INVALID_OWNER("Action not allowed for this owner"),
    INVALID_CATEGORY("The category is not valid or does not exist"),
    INVALID_PRICE("The price cannot be less than or equal to zero"),
    ILLEGAL_ARGUMENT("Illegal arguments provided"),
    ORDER_IN_PROCESS("The user already has orders in process"),
    INVALID_USER("This action is not allowed for this user"),
    INVALID_ORDER_ACTION("This action is not allowed for the current order status"),;

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
