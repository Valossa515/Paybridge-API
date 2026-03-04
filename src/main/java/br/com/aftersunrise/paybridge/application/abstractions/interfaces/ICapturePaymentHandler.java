package br.com.aftersunrise.paybridge.application.abstractions.interfaces;

import br.com.aftersunrise.paybridge.application.payments.data.CapturePaymentCommand;
import br.com.aftersunrise.paybridge.application.payments.data.PaymentResponse;

public interface ICapturePaymentHandler extends IHandler<CapturePaymentCommand, PaymentResponse> {
}
