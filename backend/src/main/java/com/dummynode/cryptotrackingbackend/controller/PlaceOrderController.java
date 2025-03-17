package com.dummynode.cryptotrackingbackend.controller;

import com.dummynode.cryptotrackingbackend.entity.dto.OrderDTO;
import com.dummynode.cryptotrackingbackend.service.ApiResponse;
import com.dummynode.cryptotrackingbackend.service.PlaceOrderService;
import com.dummynode.cryptotrackingbackend.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.annotations.CurrentTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class PlaceOrderController {
    private static final Logger logger = LoggerFactory.getLogger(PlaceOrderController.class);


    @Autowired
    PlaceOrderService placeOrderService;
    @PostMapping("/place-order")
    public ResponseEntity<Void> PlaceOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        if (orderDTO.getType() == 0) {
            logger.info("order type: sell");
            placeOrderService.sell(orderDTO);
        } else {
            logger.info("order type: buy");
            placeOrderService.buy(orderDTO);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
