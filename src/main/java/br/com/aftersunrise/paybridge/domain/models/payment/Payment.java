package br.com.aftersunrise.paybridge.domain.models.payment;


import br.com.aftersunrise.paybridge.domain.models.DatabaseEntityBase;
import br.com.aftersunrise.paybridge.domain.models.customer.CustomerInfo;
import br.com.aftersunrise.paybridge.domain.models.notifications.Notification;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentMethod;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
@Data
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Payment extends DatabaseEntityBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method; // PIX, CREDIT_CARD etc.

    private BigDecimal amount;    // value

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String description;   // softDescriptor
    private String referenceId;   // referência única do pagamento no provedor
    private String callbackUrl;   // callback para notificação
    private String returnUrl;     // URL para retorno pós-pagamento
    private String leaveUrl;      // URL caso o cliente desista
    private String channel;       // "my-channel"
    private String purchaseMode;  // "in-store" ou "online"

    @Embedded
    private CustomerInfo customer;

    @Embedded
    private Notification notification;

    private Boolean autoCapture;  // auto captura do pagamento

    private Instant createdAt;
    private Instant updatedAt;
    private Instant confirmedAt;
    private Instant cancelledAt;
    private Instant expiresAt;

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