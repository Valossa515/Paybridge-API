package br.com.aftersunrise.paybridge.api.controllers.payments;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.ICreatePaymentHandler;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IResponseEntityConverter;
import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentCommand;
import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/payments/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final ICreatePaymentHandler paymentHandler;
    private final IResponseEntityConverter responseEntityConverter;

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<CreatePaymentResponse>> createPayment(
            @RequestBody CreatePaymentCommand request) {
        return paymentHandler.execute(request)
                .thenApplyAsync(response -> responseEntityConverter.convert(response, true));
    }
}
