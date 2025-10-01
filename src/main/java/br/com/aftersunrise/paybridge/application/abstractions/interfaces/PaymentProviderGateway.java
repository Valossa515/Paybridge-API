package br.com.aftersunrise.paybridge.application.abstractions.interfaces;


import br.com.aftersunrise.paybridge.application.payments.data.PaymentResponse;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentProviderGateway {
    PaymentResponse createPayment(Payment payment);
    PaymentResponse capturePayment(String referenceId);
    PaymentResponse refundPayment(String referenceId, BigDecimal amount);
    PaymentStatus getPaymentStatus(String referenceId);
}
