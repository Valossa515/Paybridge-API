package br.com.aftersunrise.paybridge.domain.models.payment;


import br.com.aftersunrise.paybridge.domain.models.DatabaseEntityBase;
import br.com.aftersunrise.paybridge.domain.models.customer.CustomerInfo;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentMethod;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "payment")
@Builder
@Entity
public class Payment extends DatabaseEntityBase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String description;

    @Embedded
    private CustomerInfo customer;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant confirmedAt;
    private Instant cancelledAt;

    public void markAsConfirmed() {
        this.status = PaymentStatus.CONFIRMED;
        this.confirmedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
        this.updatedAt = Instant.now();
    }
}