package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.ICommand;

public record CapturePaymentCommand(
        String referenceId
) implements ICommand<PaymentResponse> {}
