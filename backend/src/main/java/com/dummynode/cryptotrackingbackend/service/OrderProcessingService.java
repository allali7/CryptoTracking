/**
 * Huaihao Mo
 */

package com.dummynode.cryptotrackingbackend.service;

import com.dummynode.cryptotrackingbackend.componet.order.BuyOrder;
import com.dummynode.cryptotrackingbackend.componet.order.Order;
import com.dummynode.cryptotrackingbackend.componet.order.SellOrder;
import com.dummynode.cryptotrackingbackend.componet.order.TransactionQueue;
import com.dummynode.cryptotrackingbackend.entity.model.*;
import com.dummynode.cryptotrackingbackend.exception.BusinessException;
import com.dummynode.cryptotrackingbackend.exception.InsufficientBalanceException;
import com.dummynode.cryptotrackingbackend.exception.InsufficientCryptoException;
import com.dummynode.cryptotrackingbackend.exception.OrderProcessingException;
import com.dummynode.cryptotrackingbackend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionSellRepository transactionSellRepository;

    @Autowired
    private TransactionBuyRepository transactionBuyRepository;

    @Autowired
    private TransactionMatchRepository transactionMatchRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionQueue<Order> transactionQueue;

    @Transactional
    public void processBuyOrder(BuyOrder buyOrder) {
        logger.info("Starting to process buy order: {}", buyOrder);
        try {
            // Request user and transaction_buy information
            User buyer = userRepository.findByUserId(buyOrder.getUserId())
                    .orElseThrow(() -> new BusinessException("Buyer not found: " + buyOrder.getUserId()));

            TransactionBuy buyTransaction = transactionBuyRepository.findById(buyOrder.getTransactionId())
                    .orElseThrow(() -> new BusinessException("Buy transaction not found: " + buyOrder.getTransactionId()));

            // Calculate total cost
            BigDecimal totalCost = buyOrder.getPrice().multiply(new BigDecimal(buyOrder.getQuantity()));
            if (buyer.getBalance().compareTo(totalCost) < 0) {
                logger.error("Insufficient balance for user: {}", buyer.getUserId());
                buyTransaction.setActive(false);
                transactionBuyRepository.save(buyTransaction);
                throw new InsufficientBalanceException("Insufficient balance for user: " + buyer.getUserId());
            }

            // Find matching sell orders (sell orders with the same symbol and price)
            List<TransactionSell> matchingSellOrders = transactionSellRepository
                    .findMatchingSellOrders(buyOrder.getSymbol(), buyOrder.getPrice());

            int remainingQuantity = buyOrder.getQuantity();
            List<TransactionMatch> matches = new ArrayList<>();

            for (TransactionSell sellOrder : matchingSellOrders) {
                if (remainingQuantity <= 0) break;

                int matchQuantity = Math.min(remainingQuantity, sellOrder.getQuantity());
                BigDecimal matchPrice = sellOrder.getDealPrice();
                BigDecimal tradeCost = matchPrice.multiply(new BigDecimal(matchQuantity));

                // Create transaction match
                TransactionMatch match = createTransactionMatch(buyTransaction, sellOrder, matchQuantity, matchPrice);
                matches.add(match);

                // Update seller account and wallet
                User seller = sellOrder.getUser();
                updateUserBalance(seller, tradeCost, true);
                updateUserWallet(seller, sellOrder.getSymbol(), matchQuantity, false);

                // Update buyer account and wallet
                updateUserBalance(buyer, tradeCost, false);
                updateUserWallet(buyer, buyOrder.getSymbol(), matchQuantity, true);

                // Update sell order status
                updateSellOrder(sellOrder, matchQuantity);

                remainingQuantity -= matchQuantity;
                logger.info("Matched buy order {} with sell order {}, quantity: {}",
                        buyOrder.getTransactionId(), sellOrder.getTransactionId(), matchQuantity);
            }

            // Save transaction matches
            transactionMatchRepository.saveAll(matches);

            // Process remaining buy order
            handleRemainingBuyOrder(buyOrder, remainingQuantity, buyTransaction);

            logger.info("Completed processing buy order: {}", buyOrder.getTransactionId());
        } catch (Exception e) {
            logger.error("Error processing buy order: {}", buyOrder.getTransactionId(), e);
            throw new OrderProcessingException("Failed to process buy order: " + e.getMessage(), e);
        }
    }

    private TransactionMatch createTransactionMatch(TransactionBuy buyTransaction, TransactionSell sellOrder,
                                                    int matchQuantity, BigDecimal matchPrice) {
        TransactionMatch match = new TransactionMatch();
        match.setBuyTransaction(buyTransaction);
        match.setSellTransaction(sellOrder);
        match.setMatchQuantity(matchQuantity);
        match.setMatchPrice(matchPrice);
        match.setMatchAmount(matchPrice.multiply(new BigDecimal(matchQuantity)));
        match.setMatchTime(LocalDateTime.now());
        return match;
    }

    /**
     * @param user     user
     * @param symbol   crypto symbol
     * @param quantity quantity
     * @param isAdd    true: add quantity, false: subtract quantity
     */
    @Transactional
    protected void updateUserWallet(User user, String symbol, int quantity, boolean isAdd) {
        Wallet wallet = walletRepository.findByUserUserIdAndSymbol(user.getUserId(), symbol)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setSymbol(symbol);
                    newWallet.setQuantity(0);
                    return newWallet;
                });

        if (isAdd) {
            wallet.setQuantity(wallet.getQuantity() + quantity);
        } else {
            if (wallet.getQuantity() < quantity) {
                throw new InsufficientCryptoException("Insufficient crypto balance for user: " + user.getUserId());
            }
            wallet.setQuantity(wallet.getQuantity() - quantity);
        }
        walletRepository.save(wallet);
    }

    /**
     * @param user   user
     * @param amount balance amount
     * @param isAdd  true: add balance, false: subtract balance
     */
    @Transactional
    protected void updateUserBalance(User user, BigDecimal amount, boolean isAdd) {
        if (isAdd) {
            user.setBalance(user.getBalance().add(amount));
        } else {
            user.setBalance(user.getBalance().subtract(amount));
        }
        userRepository.save(user);
    }

    /**
     * @param sellOrder    sell order
     * @param matchQuantity matched quantity
     */
    private void updateSellOrder(TransactionSell sellOrder, int matchQuantity) {
        int remainingSellQuantity = sellOrder.getQuantity() - matchQuantity;
        if (remainingSellQuantity == 0) {
            sellOrder.setActive(false);
        } else {
            sellOrder.setQuantity(remainingSellQuantity);
        }
        transactionSellRepository.save(sellOrder);
    }

    /**
     * @param buyOrder          buy order
     * @param remainingQuantity remaining quantity
     * @param buyTransaction    buy transaction
     *
     */
    private void handleRemainingBuyOrder(BuyOrder buyOrder, int remainingQuantity, TransactionBuy buyTransaction) {
        if (remainingQuantity > 0) {
            BuyOrder remainingOrder = new BuyOrder(
                    buyOrder.getUserId(),
                    buyOrder.getTransactionId(),
                    buyOrder.getSymbol(),
                    remainingQuantity,
                    buyOrder.getPrice()
            );
            transactionQueue.enqueue(remainingOrder);

            buyTransaction.setQuantity(remainingQuantity);
        } else {
            buyTransaction.setActive(false);
        }
        transactionBuyRepository.save(buyTransaction);
    }

    @Transactional
    public void processSellOrder(SellOrder sellOrder) {
        logger.info("Processing sell order: {}", sellOrder);
        try {
            // Request user and transaction_sell information
            User seller = userRepository.findByUserId(sellOrder.getUserId())
                    .orElseThrow(() -> new BusinessException("Seller not found: " + sellOrder.getUserId()));

            TransactionSell sellTransaction = transactionSellRepository.findById(sellOrder.getTransactionId())
                    .orElseThrow(() -> new BusinessException("Sell transaction not found: " + sellOrder.getTransactionId()));

            // Calculate total cost
            Wallet sellerWallet = walletRepository.findByUserUserIdAndSymbol(seller.getUserId(), sellOrder.getSymbol())
                    .orElseThrow(() -> new BusinessException("Seller wallet not found for symbol: " + sellOrder.getSymbol()));

            if (sellerWallet.getQuantity() < sellOrder.getQuantity()) {
                logger.error("Insufficient crypto balance for user: {}", seller.getUserId());
                sellTransaction.setActive(false);
                transactionSellRepository.save(sellTransaction);
                throw new InsufficientCryptoException("Insufficient crypto balance for user: " + seller.getUserId());
            }

            // Find matching buy orders (buy orders with the same symbol and price)
            List<TransactionBuy> matchingBuyOrders = transactionBuyRepository
                    .findMatchingBuyOrders(sellOrder.getSymbol(), sellOrder.getPrice());

            int remainingQuantity = sellOrder.getQuantity();
            List<TransactionMatch> matches = new ArrayList<>();

            for (TransactionBuy buyOrder : matchingBuyOrders) {
                if (remainingQuantity <= 0) break;

                int matchQuantity = Math.min(remainingQuantity, buyOrder.getQuantity());
                BigDecimal matchPrice = buyOrder.getDealPrice();
                BigDecimal tradeCost = matchPrice.multiply(new BigDecimal(matchQuantity));

                // Create transaction match
                TransactionMatch match = createTransactionMatch(buyOrder, sellTransaction, matchQuantity, matchPrice);
                matches.add(match);

                // Update buyer account and wallet
                User buyer = buyOrder.getUser();
                updateUserBalance(buyer, tradeCost, false);
                updateUserWallet(buyer, sellOrder.getSymbol(), matchQuantity, true);

                // Update seller account and wallet
                updateUserBalance(seller, tradeCost, true);
                updateUserWallet(seller, sellOrder.getSymbol(), matchQuantity, false);

                // update buy order status
                updateBuyOrder(buyOrder, matchQuantity);

                remainingQuantity -= matchQuantity;
                logger.info("Matched sell order {} with buy order {}, quantity: {}",
                        sellOrder.getTransactionId(), buyOrder.getTransactionId(), matchQuantity);
            }

            // Save transaction matches
            transactionMatchRepository.saveAll(matches);

            // Process remaining sell order
            handleRemainingSellOrder(sellOrder, remainingQuantity, sellTransaction);

            logger.info("Completed processing sell order: {}", sellOrder.getTransactionId());
        } catch (Exception e) {
            logger.error("Error processing sell order: {}", sellOrder.getTransactionId(), e);
            throw new OrderProcessingException("Failed to process sell order: " + e.getMessage(), e);
        }
    }

    private void updateBuyOrder(TransactionBuy buyOrder, int matchQuantity) {
        int remainingBuyQuantity = buyOrder.getQuantity() - matchQuantity;
        if (remainingBuyQuantity == 0) {
            buyOrder.setActive(false);
        } else {
            buyOrder.setQuantity(remainingBuyQuantity);
        }
        transactionBuyRepository.save(buyOrder);
    }

    private void handleRemainingSellOrder(SellOrder sellOrder, int remainingQuantity, TransactionSell sellTransaction) {
        if (remainingQuantity > 0) {
            SellOrder remainingOrder = new SellOrder(
                    sellOrder.getUserId(),
                    sellOrder.getTransactionId(),
                    sellOrder.getSymbol(),
                    remainingQuantity,
                    sellOrder.getPrice()
            );
            transactionQueue.enqueue(remainingOrder);

            sellTransaction.setQuantity(remainingQuantity);
        } else {
            sellTransaction.setActive(false);
        }
        transactionSellRepository.save(sellTransaction);
    }
}