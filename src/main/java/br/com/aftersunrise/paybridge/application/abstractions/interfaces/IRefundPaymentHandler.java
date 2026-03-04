package br.com.aftersunrise.paybridge.application.abstractions.interfaces;

import br.com.aftersunrise.paybridge.application.payments.data.PaymentResponse;
import br.com.aftersunrise.paybridge.application.payments.data.RefundPaymentCommand;

public interface IRefundPaymentHandler extends IHandler<RefundPaymentCommand, PaymentResponse> {
}
