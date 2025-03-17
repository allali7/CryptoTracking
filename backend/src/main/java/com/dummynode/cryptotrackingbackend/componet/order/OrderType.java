package com.dummynode.cryptotrackingbackend.componet.order;

import lombok.Getter;

@Getter
public enum OrderType {
    SELL(0, "sell"),
    BUY(1, "buy");

    private final int code;
    private final String description;

    OrderType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static OrderType fromCode(int code) {
        for (OrderType type : OrderType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid order type code: " + code);
    }
}
