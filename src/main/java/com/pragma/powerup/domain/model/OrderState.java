package com.pragma.powerup.domain.model;

public enum OrderState {
    PENDING("PENDING"),
    PREPARATION("PREPARATION"),
    DONE("DONE"),
    DELIVERED("DELIVERED");

    public final String label;

    private OrderState(String label){
        this.label = label;
    }
}
