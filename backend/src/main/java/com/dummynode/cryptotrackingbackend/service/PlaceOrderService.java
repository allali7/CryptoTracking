package com.dummynode.cryptotrackingbackend.service;

import com.dummynode.cryptotrackingbackend.entity.dto.OrderDTO;

public interface PlaceOrderService {
    void buy(OrderDTO orderDTO);
    void sell(OrderDTO orderDTO);
}
