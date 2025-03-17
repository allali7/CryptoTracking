package com.dummynode.cryptotrackingbackend.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletVO {
    private String symbol;
    private String quantity;
    private String avgCostPerUnit;
}
