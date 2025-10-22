package br.com.aftersunrise.paybridge.application.payments.gateways;

import br.com.aftersunrise.paybridge.application.abstractions.interfaces.PaymentProviderGateway;
import br.com.aftersunrise.paybridge.application.abstractions.models.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentProviderResolver {

    private final Map<String, PaymentProviderGateway> providersById;
    private final String defaultProviderId;

    public PaymentProviderResolver(
            List<PaymentProviderGateway> providers,
            @Value("${payment.default-provider:mock}") String defaultProviderId) {
        this.providersById = providers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        gateway -> gateway.getProviderId().toLowerCase(Locale.ROOT),
                        gateway -> gateway
                ));
        this.defaultProviderId = defaultProviderId.toLowerCase(Locale.ROOT);
    }

    public PaymentProviderGateway resolve(String requestedProviderId) {
        String key = (requestedProviderId == null || requestedProviderId.isBlank())
                ? defaultProviderId
                : requestedProviderId.toLowerCase(Locale.ROOT);

        PaymentProviderGateway gateway = providersById.get(key);
        if (gateway == null) {
            throw new BusinessException("Provedor de pagamento não suportado: " + key);
        }
        return gateway;
    }

    public String getDefaultProviderId() {
        return defaultProviderId;
    }
}
