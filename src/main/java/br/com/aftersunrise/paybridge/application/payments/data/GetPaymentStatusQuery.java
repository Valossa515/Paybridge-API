package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IQuery;

public record GetPaymentStatusQuery(
        String referenceId
) implements IQuery<PaymentStatusResponse> {}
