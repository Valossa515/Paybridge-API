package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.ICommand;
import br.com.aftersunrise.paybridge.domain.models.customer.CustomerInfo;

import java.math.BigDecimal;

public record CreatePaymentCommand(
        BigDecimal amount,
        String description,
        String callbackUrl,
        String returnUrl,
        String leaveUrl,
        String channel,
        String purchaseMode,
        CustomerInfo customer
) implements ICommand<CreatePaymentResponse> {}
