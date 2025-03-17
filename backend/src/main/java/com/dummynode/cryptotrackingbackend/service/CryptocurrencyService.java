/**
 * HuaiHao Mo
 */
package com.dummynode.cryptotrackingbackend.service;

import com.dummynode.cryptotrackingbackend.entity.vo.CryptocurrencyVO;

import java.util.List;

public interface CryptocurrencyService {

    ApiResponse<List<CryptocurrencyVO>> getLatestCryptocurrencies();
}
