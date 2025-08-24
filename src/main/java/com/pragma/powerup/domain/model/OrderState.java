package com.pragma.powerup.domain.model;

public enum OrderState {
    PENDING("Pending"),
    PREPARATION("Preparation"),
    DONE("Done"),
    DELIVERED("Delivered");

    public final String label;

    private OrderState(String label){
        this.label = label;
    }
}
