package br.com.aftersunrise.paybridge.domain.services.payments;

import br.com.aftersunrise.paybridge.application.payments.data.OAuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Slf4j
public class OAuthService {

    private final RestTemplate restTemplate;
    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;

    // Cache simples
    private String cachedToken;
    private Instant tokenExpiresAt;

    public OAuthService(RestTemplate restTemplate,
                        @Value("${picpay.oauth.token-url}") String tokenUrl,
                        @Value("${picpay.oauth.client-id}") String clientId,
                        @Value("${picpay.oauth.client-secret}") String clientSecret) {
        this.restTemplate = restTemplate;
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Retorna um token válido (reaproveita cache enquanto válido).
     * Síncrono e thread-safe via sincronização simples.
     */
    public synchronized String getAccessToken() {
        if (isTokenValid()) {
            return cachedToken;
        }
        fetchToken();
        if (!isTokenValid()) {
            throw new IllegalStateException("Não foi possível obter token OAuth.");
        }
        return cachedToken;
    }

    private boolean isTokenValid() {
        return cachedToken != null && tokenExpiresAt != null && Instant.now().isBefore(tokenExpiresAt.minusSeconds(10));
    }

    private void fetchToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(body, headers);

            ResponseEntity<OAuthResponse> resp = restTemplate.postForEntity(tokenUrl, req, OAuthResponse.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                OAuthResponse oauth = resp.getBody();
                this.cachedToken = oauth.getAccessToken();

                long expiresIn = oauth.getExpiresIn() != null ? oauth.getExpiresIn() : 3600L;
                this.tokenExpiresAt = Instant.now().plusSeconds(expiresIn);

                log.debug("Novo token obtido. Expira em {}s", expiresIn);
            } else {
                log.error("Falha ao obter token OAuth. status={} body={}", resp.getStatusCode(), resp.getBody());
                throw new IllegalStateException("Erro ao obter token OAuth: " + resp.getStatusCode());
            }
        } catch (RestClientException ex) {
            log.error("Erro HTTP ao requisitar token OAuth", ex);
            throw new IllegalStateException("Erro ao requisitar token OAuth", ex);
        }
    }

    /** Força limpar cache (útil para testes/admin). */
    public synchronized void clearCache() {
        this.cachedToken = null;
        this.tokenExpiresAt = null;
    }
}
