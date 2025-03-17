package com.dummynode.cryptotrackingbackend.componet.order;

import java.math.BigDecimal;

public interface Order {

    OrderType getType();

    Integer getQuantity();

    BigDecimal getPrice();

}
