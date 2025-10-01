package br.com.aftersunrise.paybridge.application.payments.gateways;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.PaymentProviderGateway;
import br.com.aftersunrise.paybridge.application.abstractions.models.PaymentProviderException;
import br.com.aftersunrise.paybridge.application.payments.adapters.PicPayMapper;
import br.com.aftersunrise.paybridge.application.payments.data.*;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import br.com.aftersunrise.paybridge.domain.services.payments.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class PicPayGateway implements PaymentProviderGateway {

    private final RestTemplate restTemplate;
    private final OAuthService oauthService;
    private final String baseUrl = "https://api.picpay.com/ecommerce/v2";

    public PicPayGateway(RestTemplate restTemplate, OAuthService oauthService) {
        this.restTemplate = restTemplate;
        this.oauthService = oauthService;
    }

    private HttpHeaders authHeaders() {
        String token = oauthService.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
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
                    baseUrl + "/payments",
                    req,
                    PicPayPaymentResponse.class
            );

            if (resp.getBody() == null) {
                throw new PaymentProviderException("PicPay retornou body nulo ao criar pagamento.");
            }

            return PicPayMapper.toPaymentResponse(resp.getBody());

        } catch (RestClientException ex) {
            log.error("Erro ao criar pagamento no PicPay: {}", ex.getMessage(), ex);
            throw new PaymentProviderException("Erro ao criar pagamento no PicPay", ex);
        }
    }

    // ------------------------------
    // Captura de pagamento
    // ------------------------------
    @Override
    public PaymentResponse capturePayment(String referenceId) {
        try {
            HttpEntity<Void> req = new HttpEntity<>(authHeaders());
            ResponseEntity<PicPayPaymentResponse> resp = restTemplate.postForEntity(
                    baseUrl + "/payments/" + referenceId + "/capture",
                    req,
                    PicPayPaymentResponse.class
            );

            if (resp.getBody() == null) {
                throw new PaymentProviderException("PicPay retornou body nulo ao capturar pagamento.");
            }

            return PicPayMapper.toPaymentResponse(resp.getBody());

        } catch (RestClientException ex) {
            log.error("Erro ao capturar pagamento no PicPay: {}", ex.getMessage(), ex);
            throw new PaymentProviderException("Erro ao capturar pagamento no PicPay", ex);
        }
    }

    // ------------------------------
    // Reembolso / cancelamento
    // ------------------------------
    @Override
    public PaymentResponse refundPayment(String referenceId, BigDecimal amount) {
        try {
            HttpEntity<Map<String, Object>> req =
                    new HttpEntity<>(Map.of("value", amount), authHeaders());

            ResponseEntity<PicPayPaymentResponse> resp = restTemplate.postForEntity(
                    baseUrl + "/payments/" + referenceId + "/refunds",
                    req,
                    PicPayPaymentResponse.class
            );

            if (resp.getBody() == null) {
                throw new PaymentProviderException("PicPay retornou body nulo ao reembolsar pagamento.");
            }

            return PicPayMapper.toPaymentResponse(resp.getBody());

        } catch (RestClientException ex) {
            log.error("Erro ao reembolsar pagamento no PicPay: {}", ex.getMessage(), ex);
            throw new PaymentProviderException("Erro ao reembolsar pagamento no PicPay", ex);
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
                    baseUrl + "/payments/" + referenceId + "/status",
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

            // Aqui você chamaria seu PaymentService para atualizar a entidade
            // paymentService.updatePaymentStatus(referenceId, status);

            log.info("Callback PicPay processado. referenceId={} status={}", referenceId, status);

        } catch (Exception ex) {
            log.error("Erro ao processar callback PicPay: {}", ex.getMessage(), ex);
        }
    }
}



