package br.com.aftersunrise.paybridge.application.abstractions.interfaces;

import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentCommand;
import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentResponse;

public interface ICreatePaymentHandler extends IHandler<CreatePaymentCommand, CreatePaymentResponse> {
}
