package com.dummynode.cryptotrackingbackend.service.impl;

import com.dummynode.cryptotrackingbackend.entity.dto.OrderDTO;
import com.dummynode.cryptotrackingbackend.entity.model.TransactionBuy;
import com.dummynode.cryptotrackingbackend.entity.model.TransactionSell;
import com.dummynode.cryptotrackingbackend.entity.model.User;
import com.dummynode.cryptotrackingbackend.entity.model.Wallet;
import com.dummynode.cryptotrackingbackend.entity.vo.BalanceVO;
import com.dummynode.cryptotrackingbackend.entity.vo.WalletVO;
import com.dummynode.cryptotrackingbackend.exception.UserNotFoundException;
import com.dummynode.cryptotrackingbackend.repository.TransactionBuyRepository;
import com.dummynode.cryptotrackingbackend.repository.TransactionSellRepository;
import com.dummynode.cryptotrackingbackend.repository.UserRepository;
import com.dummynode.cryptotrackingbackend.repository.WalletRepository;
import com.dummynode.cryptotrackingbackend.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionBuyRepository transactionBuyRepository;
    @Autowired
    private TransactionSellRepository transactionSellRepository;

    @Override
    @Transactional
    public void buy(OrderDTO orderDTO) {
        logger.info("Processing buy order request: {}", orderDTO);

        // Find and validate user
        User user = userRepository.findByUserId(orderDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + orderDTO.getUserId()));
        logger.info("user found"+user.getUserId());
        Wallet wallet = new Wallet();
        wallet.setQuantity(orderDTO.getQuantity());
        wallet.setUser(user);
        wallet.setCostPerUnit(orderDTO.getPrice());
        wallet.setRemainQuantity(orderDTO.getQuantity());
        wallet.setSymbol(orderDTO.getSymbol());
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setModifiedAt(LocalDateTime.now());
        logger.info("setted wallet");
        walletRepository.save(wallet);
        logger.info("saved wallet");

        logger.info("calculate new balance for buy");
        BigDecimal balance = user.getBalance();
        BigDecimal cost = orderDTO.getPrice().multiply(BigDecimal.valueOf(orderDTO.getQuantity()));
        balance = balance.subtract(cost);
        user.setBalance(balance);
        userRepository.save(user);
        logger.info("saved new balance");

        TransactionBuy transactionBuy = new TransactionBuy();
        transactionBuy.setUser(user);
        transactionBuy.setSymbol(orderDTO.getSymbol());
        transactionBuy.setDealPrice(orderDTO.getPrice());
        transactionBuy.setQuantity(orderDTO.getQuantity());
        transactionBuy.setCreatedAt(LocalDateTime.now());
        transactionBuy.setModifiedAt(LocalDateTime.now());
        transactionBuyRepository.save(transactionBuy);

    }
    @Override
    @Transactional
    public void sell(OrderDTO orderDTO) {
        logger.info("Processing sell order request: {}", orderDTO);

        // Find and validate user
        User user = userRepository.findByUserId(orderDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + orderDTO.getUserId()));
        List<Wallet> wallets= walletRepository.findByUserIdAndSymbol(user.getUserId(), orderDTO.getSymbol());
        Integer totalSold = 0;
        BigDecimal totalCost = BigDecimal.valueOf(0.0);
        for(Wallet wallet: wallets){
            if(totalSold >= orderDTO.getQuantity()) break;
            Integer walletAvailable = wallet.getRemainQuantity();
            Integer walletToSell = Math.min(walletAvailable,orderDTO.getQuantity() - totalSold);

            //update remaining quantity
            wallet.setRemainQuantity(walletAvailable - walletToSell);
            walletRepository.save(wallet);

            logger.info("calculate new balance for sell");
            BigDecimal balance = user.getBalance();
            BigDecimal cost = orderDTO.getPrice().multiply(BigDecimal.valueOf(orderDTO.getQuantity()));
            balance = balance.add(cost);
            user.setBalance(balance);
            userRepository.save(user);
            logger.info("saved new balance");

            TransactionSell transactionSell = new TransactionSell();
            transactionSell.setUser(user);
            transactionSell.setSymbol(orderDTO.getSymbol());
            transactionSell.setDealPrice(orderDTO.getPrice());
            transactionSell.setQuantity(orderDTO.getQuantity());
            transactionSell.setCreatedAt(LocalDateTime.now());
            transactionSell.setModifiedAt(LocalDateTime.now());
            transactionSellRepository.save(transactionSell);

            totalCost = totalCost.add(BigDecimal.valueOf(walletToSell).multiply(wallet.getCostPerUnit()));
            totalSold += walletToSell;


        }
        if (totalSold < orderDTO.getQuantity()) {
            throw new IllegalArgumentException("Insufficient quantity to sell.");
        }



    }

    public List<WalletVO> getWallet(String userId){
        logger.info("getting wallet by userId: "+userId);
        List<WalletVO> walletVOList = new ArrayList<>();
        List<Map<String, Object>> walletList = walletRepository.findByUserId(userId);
        for(Map<String,Object> map : walletList){
            WalletVO walletVO = new WalletVO();
            walletVO.setSymbol(String.valueOf(map.get("symbol")));
            walletVO.setQuantity(String.valueOf(map.get("quantity")));
            walletVO.setAvgCostPerUnit(String.valueOf(map.get("avg_cost_per_unit")));
            walletVOList.add(walletVO);
        }
        return walletVOList;
    }
    public BalanceVO getBalance(String userId){
        User balance= userRepository.findById(userId).orElse(null);

        BalanceVO balanceVO = new BalanceVO();
        balanceVO.setBalance(String.valueOf(balance.getBalance()));
        return balanceVO;
    }
}
