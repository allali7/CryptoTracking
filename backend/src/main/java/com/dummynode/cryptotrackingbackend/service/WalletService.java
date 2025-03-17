package com.dummynode.cryptotrackingbackend.service;

import com.dummynode.cryptotrackingbackend.entity.dto.OrderDTO;
import com.dummynode.cryptotrackingbackend.entity.vo.BalanceVO;
import com.dummynode.cryptotrackingbackend.entity.vo.WalletVO;

import java.util.List;

public interface WalletService {
    void buy(OrderDTO orderDTO);
    void sell(OrderDTO orderDTO);

    List<WalletVO> getWallet(String userId);

    BalanceVO getBalance(String userId);
}
