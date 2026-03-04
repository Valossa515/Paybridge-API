package br.com.aftersunrise.paybridge.api.controllers.payments;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.ICapturePaymentHandler;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.ICreatePaymentHandler;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IGetPaymentStatusHandler;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IRefundPaymentHandler;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IResponseEntityConverter;
import br.com.aftersunrise.paybridge.application.payments.data.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/payments/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final ICreatePaymentHandler createPaymentHandler;
    private final IGetPaymentStatusHandler getPaymentStatusHandler;
    private final ICapturePaymentHandler capturePaymentHandler;
    private final IRefundPaymentHandler refundPaymentHandler;
    private final IResponseEntityConverter responseEntityConverter;

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<CreatePaymentResponse>> createPayment(
            @RequestBody CreatePaymentCommand request) {
        return createPaymentHandler.execute(request)
                .thenApplyAsync(response -> responseEntityConverter.convert(response, true));
    }

    @GetMapping("/{referenceId}/status")
    public CompletableFuture<ResponseEntity<PaymentStatusResponse>> getPaymentStatus(
            @PathVariable String referenceId) {
        return getPaymentStatusHandler.execute(new GetPaymentStatusQuery(referenceId))
                .thenApplyAsync(response -> responseEntityConverter.convert(response, true));
    }

    @PostMapping("/{referenceId}/capture")
    public CompletableFuture<ResponseEntity<PaymentResponse>> capturePayment(
            @PathVariable String referenceId) {
        return capturePaymentHandler.execute(new CapturePaymentCommand(referenceId))
                .thenApplyAsync(response -> responseEntityConverter.convert(response, true));
    }

    @PostMapping("/{referenceId}/refunds")
    public CompletableFuture<ResponseEntity<PaymentResponse>> refundPayment(
            @PathVariable String referenceId,
            @RequestBody RefundPaymentCommand request) {
        return refundPaymentHandler.execute(new RefundPaymentCommand(referenceId, request.amount()))
                .thenApplyAsync(response -> responseEntityConverter.convert(response, true));
    }
}
