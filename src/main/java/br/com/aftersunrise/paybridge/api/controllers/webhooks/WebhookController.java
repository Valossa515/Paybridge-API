package br.com.aftersunrise.paybridge.api.controllers.webhooks;

import br.com.aftersunrise.paybridge.application.abstractions.models.BusinessException;
import br.com.aftersunrise.paybridge.application.payments.data.PicPayCallbackNotification;
import br.com.aftersunrise.paybridge.application.payments.gateways.PaymentProviderResolver;
import br.com.aftersunrise.paybridge.application.payments.gateways.PicPayGateway;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import br.com.aftersunrise.paybridge.infrastructure.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentProviderResolver paymentProviderResolver;
    private final PaymentRepository paymentRepository;

    @PostMapping("/picpay")
    public ResponseEntity<Void> picpayCallback(@RequestBody PicPayCallbackNotification notification) {
        try {
            String referenceId = notification.getReferenceId();
            log.info("Recebido callback PicPay para referenceId={}", referenceId);

            var gateway = paymentProviderResolver.resolve(PicPayGateway.PROVIDER_ID);
            PaymentStatus providerStatus = gateway.getPaymentStatus(referenceId);

            paymentRepository.findByReferenceId(referenceId).ifPresent(payment -> {
                payment.setStatus(providerStatus);
                payment.setUpdatedAt(Instant.now());
                if (providerStatus == PaymentStatus.CONFIRMED) {
                    payment.setConfirmedAt(Instant.now());
                }
                paymentRepository.save(payment);
                log.info("Pagamento atualizado via callback. referenceId={} status={}", referenceId, providerStatus);
            });

            return ResponseEntity.ok().build();

        } catch (BusinessException ex) {
            log.warn("Erro de negócio ao processar callback PicPay: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            log.error("Erro ao processar callback PicPay", ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}
