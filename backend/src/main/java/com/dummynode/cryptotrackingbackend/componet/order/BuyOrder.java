package com.dummynode.cryptotrackingbackend.componet.order;

import com.dummynode.cryptotrackingbackend.entity.model.TransactionBuy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BuyOrder implements Order {
    private final String userId;
    private final Long transactionId;
    private final String symbol;
    private final Integer quantity;
    private final BigDecimal price;


    @Override
    public OrderType getType() {
        return OrderType.BUY;
    }

    public static BuyOrder from(TransactionBuy transactionBuy) {
        return new BuyOrder(
                transactionBuy.getUser().getUserId(),
                transactionBuy.getTransactionId(),
                transactionBuy.getSymbol(),
                transactionBuy.getQuantity(),
                transactionBuy.getDealPrice()
        );
    }
}