/**
 * HuaiHao Mo
 */
package com.dummynode.cryptotrackingbackend.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptocurrencyVO {

    private String name;

    private String symbol;

    private BigDecimal price;

    private BigDecimal percentChange24h;

    private BigDecimal marketCap;

    private BigDecimal volume24h;

    private BigDecimal maxSupply;

    private String formattedMaxSupply;
}
