package br.com.aftersunrise.paybridge.application.payments.gateways;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.PaymentProviderGateway;
import br.com.aftersunrise.paybridge.application.abstractions.models.PaymentProviderException;
import br.com.aftersunrise.paybridge.application.payments.adapters.PicPayMapper;
import br.com.aftersunrise.paybridge.application.payments.data.*;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Component
@Slf4j
public class PicPayGateway implements PaymentProviderGateway {

    public static final String PROVIDER_ID = "picpay";
    private static final String BASE_URL = "https://appws.picpay.com/ecommerce/public";

    private final RestTemplate restTemplate;
    private final String picPayToken;

    public PicPayGateway(
            RestTemplate restTemplate,
            @Value("${picpay.x-picpay-token}") String picPayToken) {
        this.restTemplate = restTemplate;
        this.picPayToken = picPayToken;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-picpay-token", picPayToken);
        return headers;
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    // ------------------------------
    // Criar pagamento
    // ------------------------------
    @Override
    public PaymentResponse createPayment(Payment payment) {
        try {
            HttpEntity<PicPayPaymentRequest> req =
                    new HttpEntity<>(PicPayPaymentRequest.from(payment), authHeaders());

            ResponseEntity<PicPayPaymentResponse> resp = restTemplate.postForEntity(
                    BASE_URL + "/payments",
                    req,
                    PicPayPaymentResponse.class
            );

            if (resp.getBody() == null) {
                throw new PaymentProviderException("PicPay retornou body nulo ao criar pagamento.");
            }

            PaymentResponse response = PicPayMapper.toPaymentResponse(resp.getBody());
            response.setProvider(PROVIDER_ID);
            response.setAmount(payment.getAmount());
            response.setDescription(payment.getDescription());
            response.setCreatedAt(Instant.now());
            response.setUpdatedAt(Instant.now());
            return response;

        } catch (RestClientException ex) {
            log.error("Erro ao criar pagamento no PicPay: {}", ex.getMessage(), ex);
            throw new PaymentProviderException("Erro ao criar pagamento no PicPay", ex);
        }
    }

    // ------------------------------
    // Captura de pagamento (não suportado pelo PicPay — pagamentos são assíncronos)
    // ------------------------------
    @Override
    public PaymentResponse capturePayment(String referenceId) {
        throw new UnsupportedOperationException(
                "PicPay não suporta captura manual. Pagamentos são confirmados de forma assíncrona via callback.");
    }

    // ------------------------------
    // Cancelamento / reembolso
    // ------------------------------
    @Override
    public PaymentResponse refundPayment(String referenceId, BigDecimal amount) {
        try {
            HttpEntity<Map<String, Object>> req =
                    new HttpEntity<>(Map.of("authorizationId", referenceId), authHeaders());

            ResponseEntity<PicPayPaymentResponse> resp = restTemplate.postForEntity(
                    BASE_URL + "/payments/" + referenceId + "/cancellations",
                    req,
                    PicPayPaymentResponse.class
            );

            if (resp.getBody() == null) {
                throw new PaymentProviderException("PicPay retornou body nulo ao cancelar pagamento.");
            }

            PaymentResponse response = PicPayMapper.toPaymentResponse(resp.getBody());
            response.setProvider(PROVIDER_ID);
            response.setUpdatedAt(Instant.now());
            return response;

        } catch (RestClientException ex) {
            log.error("Erro ao cancelar pagamento no PicPay: {}", ex.getMessage(), ex);
            throw new PaymentProviderException("Erro ao cancelar pagamento no PicPay", ex);
        }
    }

    // ------------------------------
    // Consultar status do pagamento
    // ------------------------------
    @Override
    public PaymentStatus getPaymentStatus(String referenceId) {
        try {
            HttpEntity<Void> req = new HttpEntity<>(authHeaders());

            ResponseEntity<PicPayStatusResponse> resp = restTemplate.exchange(
                    BASE_URL + "/payments/" + referenceId + "/status",
                    HttpMethod.GET,
                    req,
                    PicPayStatusResponse.class
            );

            if (resp.getBody() == null) {
                throw new PaymentProviderException("PicPay retornou body nulo ao consultar status.");
            }

            return PicPayMapper.toPaymentStatus(resp.getBody().getStatus());

        } catch (RestClientException ex) {
            log.error("Erro ao consultar status de pagamento no PicPay: {}", ex.getMessage(), ex);
            throw new PaymentProviderException("Erro ao consultar status de pagamento no PicPay", ex);
        }
    }

    // ------------------------------
    // Webhook handler
    // ------------------------------
    public void handleCallback(PicPayCallbackNotification notif) {
        try {
            String referenceId = notif.getReferenceId();
            PaymentStatus status = getPaymentStatus(referenceId);

            log.info("Callback PicPay processado. referenceId={} status={}", referenceId, status);

        } catch (Exception ex) {
            log.error("Erro ao processar callback PicPay: {}", ex.getMessage(), ex);
        }
    }
}



