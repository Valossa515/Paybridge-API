package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String referenceId;      // referência interna / no provedor
    private PaymentStatus status;    // status padronizado (CONFIRMED, PENDING, FAILED, REFUNDED)
    private String provider;         // provedor usado (picpay, paypal, mercado_pago)
    private String paymentUrl;       // link de checkout/redirecionamento
    private String qrCode;           // QR Code em texto (se aplicável)
    private String qrCodeBase64;     // QR Code em imagem Base64 (se aplicável)
    private BigDecimal amount;       // valor
    private String description;      // descrição curta
    private Instant createdAt;
    private Instant updatedAt;
}
