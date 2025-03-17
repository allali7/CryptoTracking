package com.dummynode.cryptotrackingbackend.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class OrderDTO {
    private String userId;
    private Integer cryptoId;
    private String symbol;
    private Integer type;
    private Integer quantity;
    private BigDecimal price;
}
