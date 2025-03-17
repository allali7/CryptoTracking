/**
 * Christopher Park
 */
package com.dummynode.cryptotrackingbackend.entity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction_match")
public class TransactionMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "buy_transaction_id", nullable = false)
    private TransactionBuy buyTransaction;

    @ManyToOne
    @JoinColumn(name = "sell_transaction_id", nullable = false)
    private TransactionSell sellTransaction;

    @Column(name = "match_quantity", nullable = false)
    private Integer matchQuantity;

    @Column(name = "match_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal matchPrice;

    @Column(name = "match_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal matchAmount;

    @Column(name = "match_time", nullable = false, updatable = false)
    private LocalDateTime matchTime;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @PrePersist
    protected void onCreate() {
        this.matchTime = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TransactionMatch{" +
                "matchId=" + matchId +
                ", buyTransaction=" + buyTransaction.getTransactionId() +
                ", sellTransaction=" + sellTransaction.getTransactionId() +
                ", matchQuantity=" + matchQuantity +
                ", matchPrice=" + matchPrice +
                ", matchAmount=" + matchAmount +
                ", matchTime=" + matchTime +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}