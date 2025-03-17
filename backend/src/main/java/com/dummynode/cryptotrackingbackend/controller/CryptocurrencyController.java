/**
 * HuaiHao Mo
 */
package com.dummynode.cryptotrackingbackend.controller;


import com.dummynode.cryptotrackingbackend.entity.vo.CryptocurrencyVO;
import com.dummynode.cryptotrackingbackend.entity.vo.wrapper.CryptocurrencyVOWrapper;
import com.dummynode.cryptotrackingbackend.service.ApiResponse;
import com.dummynode.cryptotrackingbackend.service.CryptocurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("api/v1/home")
public class CryptocurrencyController {

    private final CryptocurrencyService cryptocurrencyService;

    @Autowired
    public CryptocurrencyController(CryptocurrencyService cryptocurrencyService) {
        this.cryptocurrencyService = cryptocurrencyService;
    }

    @GetMapping("/cryptocurrencies")
    public ApiResponse<List<CryptocurrencyVO>> getCryptocurrencies() {
        return cryptocurrencyService.getLatestCryptocurrencies();
    }
}
