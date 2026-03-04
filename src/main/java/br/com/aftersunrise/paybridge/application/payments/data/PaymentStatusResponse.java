package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;

public record PaymentStatusResponse(
        String referenceId,
        PaymentStatus status,
        String provider
) {}
