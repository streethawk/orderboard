package com.harshsetia.orderboard.model;

/**
 * Created by harshsetia on 10/02/2017.
 */
public enum OrderType {
    SELL("SELL"),
    BUY("BUY");

    private String value;
    private OrderType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

}
