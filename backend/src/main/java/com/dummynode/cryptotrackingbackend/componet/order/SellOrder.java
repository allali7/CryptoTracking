package com.dummynode.cryptotrackingbackend.componet.order;

import com.dummynode.cryptotrackingbackend.entity.model.TransactionSell;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SellOrder implements Order {

    private final String userId;
    private final Long transactionId;
    private final String symbol;
    private final Integer quantity;
    private final BigDecimal price;

    @Override
    public OrderType getType() {
        return OrderType.SELL;
    }

    public static SellOrder from(TransactionSell transactionSell) {
        return new SellOrder(
                transactionSell.getUser().getUserId(),
                transactionSell.getTransactionId(),
                transactionSell.getSymbol(),
                transactionSell.getQuantity(),
                transactionSell.getDealPrice()
        );
    }
}