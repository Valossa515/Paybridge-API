package br.com.aftersunrise.paybridge.application.payments.adapters;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IPaymentAdapter;
import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentCommand;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentAdapter implements IPaymentAdapter {

    @Override
    public Payment toPayment(CreatePaymentCommand command) {
        return Payment.builder()
                .amount(command.amount())
                .description(command.description())
                .callbackUrl(command.callbackUrl())
                .returnUrl(command.returnUrl())
                .leaveUrl(command.leaveUrl())
                .channel(command.channel())
                .purchaseMode(command.purchaseMode())
                .provider(command.provider())
                .customer(command.customer())
                .autoCapture(true) // por padrão
                .build();
    }
}
