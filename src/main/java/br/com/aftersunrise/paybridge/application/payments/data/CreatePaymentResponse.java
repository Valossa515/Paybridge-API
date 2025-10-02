package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;

public record CreatePaymentResponse(
        String referenceId,
        PaymentStatus status,
        String paymentUrl,
        String qrCode,
        String qrCodeBase64
) { }
