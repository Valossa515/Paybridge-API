package br.com.aftersunrise.paybridge.application.abstractions.interfaces;

import br.com.aftersunrise.paybridge.application.payments.data.GetPaymentStatusQuery;
import br.com.aftersunrise.paybridge.application.payments.data.PaymentStatusResponse;

public interface IGetPaymentStatusHandler extends IHandler<GetPaymentStatusQuery, PaymentStatusResponse> {
}
