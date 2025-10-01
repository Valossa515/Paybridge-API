package br.com.aftersunrise.paybridge.application.payments.data;

import br.com.aftersunrise.paybridge.application.customers.data.PicPayBuyer;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PicPayPaymentRequest {
    private String referenceId;
    private String callbackUrl;
    private String returnUrl;
    private BigDecimal value;
    private PicPayBuyer buyer;

    public static PicPayPaymentRequest from(Payment payment) {
        return new PicPayPaymentRequest(
                payment.getReferenceId(),
                payment.getCallbackUrl(),
                payment.getReturnUrl(),
                payment.getAmount(),
                new PicPayBuyer(
                        payment.getCustomer().getFirstName(),
                        payment.getCustomer().getLastName(),
                        payment.getCustomer().getDocument(),
                        payment.getCustomer().getEmail(),
                        payment.getCustomer().getPhone()
                )
        );
    }
}
