package br.com.aftersunrise.paybridge.application.payments.gateways;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.PaymentProviderGateway;
import br.com.aftersunrise.paybridge.application.abstractions.models.BusinessException;
import br.com.aftersunrise.paybridge.application.payments.data.PaymentResponse;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockPaymentGateway implements PaymentProviderGateway {

    public static final String PROVIDER_ID = "mock";

    private final Map<String, PaymentResponse> payments = new ConcurrentHashMap<>();

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public PaymentResponse createPayment(Payment payment) {
        String referenceId = payment.getReferenceId();
        if (referenceId == null || referenceId.isBlank()) {
            referenceId = UUID.randomUUID().toString();
        }

        Instant now = Instant.now();
        String qrCode = "000201MOCK" + referenceId;
        String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCode.getBytes(StandardCharsets.UTF_8));

        PaymentResponse response = PaymentResponse.builder()
                .referenceId(referenceId)
                .status(PaymentStatus.PENDING)
                .provider(PROVIDER_ID)
                .paymentUrl("https://mock.paybridge.local/checkout/" + referenceId)
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .qrCode(qrCode)
                .qrCodeBase64(qrCodeBase64)
                .createdAt(now)
                .updatedAt(now)
                .build();

        payments.put(referenceId, response);
        return response;
    }

    @Override
    public PaymentResponse capturePayment(String referenceId) {
        PaymentResponse existing = getPayment(referenceId);
        PaymentResponse updated = existing.toBuilder()
                .status(PaymentStatus.CONFIRMED)
                .updatedAt(Instant.now())
                .build();
        payments.put(referenceId, updated);
        return updated;
    }

    @Override
    public PaymentResponse refundPayment(String referenceId, BigDecimal amount) {
        PaymentResponse existing = getPayment(referenceId);
        PaymentResponse updated = existing.toBuilder()
                .status(PaymentStatus.REFUNDED)
                .updatedAt(Instant.now())
                .build();
        payments.put(referenceId, updated);
        return updated;
    }

    @Override
    public PaymentStatus getPaymentStatus(String referenceId) {
        return getPayment(referenceId).getStatus();
    }

    private PaymentResponse getPayment(String referenceId) {
        PaymentResponse response = payments.get(referenceId);
        if (response == null) {
            throw new BusinessException("Pagamento não encontrado para referência: " + referenceId);
        }
        return response;
    }
}
