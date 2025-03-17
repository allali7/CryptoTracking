package com.dummynode.cryptotrackingbackend;

import com.dummynode.cryptotrackingbackend.config.RestTemplateConfig;
import com.dummynode.cryptotrackingbackend.tool.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@SpringBootTest
class CryptoTrackingBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void restTemplateTest() {
        RestTemplate restTemplate = new RestTemplateConfig().restTemplate();
        String testUrl = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=100&sort=market_cap&cryptocurrency_type=all&tag=all";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(testUrl, String.class);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void longIntFormatterTest() {
        BigDecimal num = BigDecimal.valueOf(15L);
        Assertions.assertEquals(Util.NumberFormatter.format(num), "1.5K");
    }

}
