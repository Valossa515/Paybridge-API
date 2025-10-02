package br.com.aftersunrise.paybridge.application.abstractions.interfaces;

import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentCommand;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;

public interface IPaymentAdapter {
    Payment toPayment(CreatePaymentCommand command);
}
