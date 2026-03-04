package br.com.aftersunrise.paybridge.domain.services.payments;

import br.com.aftersunrise.paybridge.application.abstractions.data.HandlerResponseWithResult;
import br.com.aftersunrise.paybridge.application.abstractions.handlers.QueryHandlerBase;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IGetPaymentStatusHandler;
import br.com.aftersunrise.paybridge.application.abstractions.models.BusinessException;
import br.com.aftersunrise.paybridge.application.payments.data.GetPaymentStatusQuery;
import br.com.aftersunrise.paybridge.application.payments.data.PaymentStatusResponse;
import br.com.aftersunrise.paybridge.application.payments.gateways.PaymentProviderResolver;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import br.com.aftersunrise.paybridge.infrastructure.repositories.PaymentRepository;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class GetPaymentStatusQueryHandler extends QueryHandlerBase<GetPaymentStatusQuery, PaymentStatusResponse>
        implements IGetPaymentStatusHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetPaymentStatusQueryHandler.class);

    private final PaymentRepository paymentRepository;
    private final PaymentProviderResolver paymentProviderResolver;

    public GetPaymentStatusQueryHandler(
            Validator validator,
            PaymentRepository paymentRepository,
            PaymentProviderResolver paymentProviderResolver) {
        super(logger, validator);
        this.paymentRepository = paymentRepository;
        this.paymentProviderResolver = paymentProviderResolver;
    }

    @Override
    protected CompletableFuture<HandlerResponseWithResult<PaymentStatusResponse>> doExecute(GetPaymentStatusQuery request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Payment payment = paymentRepository.findByReferenceId(request.referenceId())
                        .orElseThrow(() -> new BusinessException(
                                "Pagamento não encontrado para referência: " + request.referenceId()));

                var gateway = paymentProviderResolver.resolve(payment.getProvider());
                PaymentStatus providerStatus = gateway.getPaymentStatus(request.referenceId());

                if (providerStatus != payment.getStatus()) {
                    payment.setStatus(providerStatus);
                    payment.setUpdatedAt(Instant.now());
                    paymentRepository.save(payment);
                }

                return success(new PaymentStatusResponse(
                        payment.getReferenceId(),
                        providerStatus,
                        payment.getProvider()
                ));

            } catch (BusinessException ex) {
                logger.warn("Pagamento não encontrado: {}", ex.getMessage());
                return notFound(ex.getMessage(), "NOT_FOUND");
            } catch (Exception ex) {
                logger.error("Erro ao consultar status do pagamento", ex);
                return internalServerError("STATUS_ERROR", "Erro ao consultar status do pagamento");
            }
        });
    }
}
