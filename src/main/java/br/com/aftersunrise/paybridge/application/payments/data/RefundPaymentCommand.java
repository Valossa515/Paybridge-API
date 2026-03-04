package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.ICommand;

import java.math.BigDecimal;

public record RefundPaymentCommand(
        String referenceId,
        BigDecimal amount
) implements ICommand<PaymentResponse> {}
