package com.dummynode.cryptotrackingbackend.service.impl;

import com.dummynode.cryptotrackingbackend.componet.order.BuyOrder;
import com.dummynode.cryptotrackingbackend.componet.order.Order;
import com.dummynode.cryptotrackingbackend.componet.order.SellOrder;
import com.dummynode.cryptotrackingbackend.componet.order.TransactionQueue;
import com.dummynode.cryptotrackingbackend.entity.dto.OrderDTO;
import com.dummynode.cryptotrackingbackend.entity.model.*;
import com.dummynode.cryptotrackingbackend.exception.BusinessException;
import com.dummynode.cryptotrackingbackend.exception.OrderProcessingException;
import com.dummynode.cryptotrackingbackend.exception.UserNotFoundException;
import com.dummynode.cryptotrackingbackend.repository.*;
import com.dummynode.cryptotrackingbackend.service.OrderProcessingService;
import com.dummynode.cryptotrackingbackend.service.PlaceOrderService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class PlaceOrderServiceImpl implements PlaceOrderService {

    private final OrderProcessingService orderProcessingService;
    private final UserRepository userRepository;
    private final TransactionSellRepository transactionSellRepository;
    private final TransactionBuyRepository transactionBuyRepository;
    private final TransactionQueue<Order> transactionQueue;

    @Autowired
    public PlaceOrderServiceImpl(OrderProcessingService orderProcessingService,
                                 UserRepository userRepository,
                                 TransactionSellRepository transactionSellRepository,
                                 TransactionBuyRepository transactionBuyRepository,
                                 TransactionQueue<Order> transactionQueue) {
        this.orderProcessingService = orderProcessingService;
        this.userRepository = userRepository;
        this.transactionSellRepository = transactionSellRepository;
        this.transactionBuyRepository = transactionBuyRepository;
        this.transactionQueue = transactionQueue;
    }

    @Override
    @Transactional
    public void buy(OrderDTO orderDTO) {
        log.info("Processing buy order request: {}", orderDTO);

        // Validate input parameters
        validateOrderDTO(orderDTO);

        // Find and validate user
        User user = userRepository.findByUserId(orderDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + orderDTO.getUserId()));

        try {
            // Create and save transaction
            TransactionBuy transactionBuy = createBuyTransaction(orderDTO, user);
            TransactionBuy savedTransaction = transactionBuyRepository.save(transactionBuy);

            // Create and enqueue order
            BuyOrder buyOrder = BuyOrder.from(savedTransaction);
            transactionQueue.enqueue(buyOrder);

            log.info("Buy order successfully created and enqueued: transactionId={}", savedTransaction.getTransactionId());

        } catch (Exception e) {
            log.error("Failed to process buy order: {}", orderDTO, e);
            throw new OrderProcessingException("Failed to process buy order", e);
        }
    }

    @Override
    @Transactional
    public void sell(OrderDTO orderDTO) {
        log.info("Processing sell order request: {}", orderDTO);

        // Validate input parameters
        validateOrderDTO(orderDTO);

        // Find and validate user
        User user = userRepository.findByUserId(orderDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + orderDTO.getUserId()));

        try {
            // Create and save transaction
            TransactionSell transactionSell = createSellTransaction(orderDTO, user);
            TransactionSell savedTransaction = transactionSellRepository.save(transactionSell);

            // Create and enqueue order
            SellOrder sellOrder = SellOrder.from(savedTransaction);
            transactionQueue.enqueue(sellOrder);

            log.info("Sell order successfully created and enqueued: transactionId={}", savedTransaction.getTransactionId());

        } catch (Exception e) {
            log.error("Failed to process sell order: {}", orderDTO, e);
            throw new OrderProcessingException("Failed to process sell order", e);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void processOrders() {
        try {
            if (transactionQueue.isEmpty()) {
                return;
            }

            log.debug("Start processing orders from queue");
            int processedCount = 0;
            int errorCount = 0;

            while (!transactionQueue.isEmpty()) {
                Order order = transactionQueue.dequeue();
                if (order == null) continue;

                try {
                    processOrder(order);
                    processedCount++;
                } catch (Exception e) {
                    errorCount++;
                    handleOrderProcessingError(order, e);
                }
            }

            log.info("Completed order processing: processed={}, errors={}", processedCount, errorCount);

        } catch (Exception e) {
            log.error("Critical error in order processing", e);
        }
    }

    private void processOrder(Order order) {
        try {
            if (order instanceof BuyOrder) {
                orderProcessingService.processBuyOrder((BuyOrder) order);
            } else if (order instanceof SellOrder) {
                orderProcessingService.processSellOrder((SellOrder) order);
            } else {
                throw new IllegalArgumentException("Unknown order type: " + order.getClass());
            }
        } catch (Exception e) {
            throw new OrderProcessingException("Failed to process order: " + order, e);
        }
    }

    private void handleOrderProcessingError(Order order, Exception e) {
        log.error("Error processing order: {}", order, e);
        if (e instanceof BusinessException) {
            handleBusinessException(order, (BusinessException) e);
        } else {
            handleSystemException(order, e);
        }
    }

    private void validateOrderDTO(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("Order DTO cannot be null");
        }
        if (StringUtils.isBlank(orderDTO.getUserId())) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (StringUtils.isBlank(orderDTO.getSymbol())) {
            throw new IllegalArgumentException("Symbol cannot be empty");
        }
        if (orderDTO.getQuantity() == null || orderDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        if (orderDTO.getPrice() == null || orderDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }
    }

    private TransactionBuy createBuyTransaction(OrderDTO orderDTO, User user) {
        TransactionBuy transaction = new TransactionBuy();
        transaction.setUser(user);
        transaction.setSymbol(orderDTO.getSymbol().toUpperCase());
        transaction.setDealPrice(orderDTO.getPrice());
        transaction.setQuantity(orderDTO.getQuantity());
        transaction.setActive(true);
        return transaction;
    }

    private TransactionSell createSellTransaction(OrderDTO orderDTO, User user) {
        TransactionSell transaction = new TransactionSell();
        transaction.setUser(user);
        transaction.setSymbol(orderDTO.getSymbol().toUpperCase());
        transaction.setDealPrice(orderDTO.getPrice());
        transaction.setQuantity(orderDTO.getQuantity());
        transaction.setActive(true);
        return transaction;
    }

    private void handleBusinessException(Order order, BusinessException e) {
        log.warn("Business exception while processing order: {}, error: {}", order, e.getMessage());
    }

    private void handleSystemException(Order order, Exception e) {
        log.error("System exception while processing order: {}", order, e);
    }
}