/**
 * HuaiHao Mo
 */
package com.dummynode.cryptotrackingbackend.service.impl;

import com.dummynode.cryptotrackingbackend.entity.dto.CryptocurrencyDTO;
import com.dummynode.cryptotrackingbackend.entity.vo.CryptocurrencyVO;
import com.dummynode.cryptotrackingbackend.entity.vo.wrapper.CryptocurrencyVOWrapper;
import com.dummynode.cryptotrackingbackend.service.ApiResponse;
import com.dummynode.cryptotrackingbackend.service.CryptocurrencyService;
import com.dummynode.cryptotrackingbackend.tool.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CryptocurrencyServiceImpl implements CryptocurrencyService {


    private RestTemplate restTemplate;

    @Autowired
    public CryptocurrencyServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ApiResponse<List<CryptocurrencyVO>> getLatestCryptocurrencies() {
        String testUrl = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=100&sort=market_cap&cryptocurrency_type=all&tag=all";
        CryptocurrencyDTO cryptocurrencyDTO = restTemplate.getForObject(testUrl, CryptocurrencyDTO.class);
        assert cryptocurrencyDTO != null;
        List<CryptocurrencyDTO.Cryptocurrency> data = cryptocurrencyDTO.getData();

        List<CryptocurrencyVO> cryptocurrencyVOList = data.stream()
                .map(Util::toCryptocurrencyVO)
                .toList();

        ApiResponse<List<CryptocurrencyVO>> response = new ApiResponse<>();
        response.setCode(200);
        response.setData(cryptocurrencyVOList);
        return response;
    }
}
