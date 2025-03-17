package com.dummynode.cryptotrackingbackend.repository;

import com.dummynode.cryptotrackingbackend.entity.model.TransactionMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionMatchRepository extends JpaRepository<TransactionMatch, Long> {
    List<TransactionMatch> findByBuyTransactionTransactionId(@Param("buyTransactionId") Long buyTransactionId);
    List<TransactionMatch> findBySellTransactionTransactionId(@Param("sellTransactionId") Long sellTransactionId);
}
