package br.com.aftersunrise.paybridge.domain.services.payments;

import br.com.aftersunrise.paybridge.application.abstractions.data.HandlerResponseWithResult;
import br.com.aftersunrise.paybridge.application.abstractions.handlers.CommandHandlerBase;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IRefundPaymentHandler;
import br.com.aftersunrise.paybridge.application.abstractions.models.BusinessException;
import br.com.aftersunrise.paybridge.application.payments.data.PaymentResponse;
import br.com.aftersunrise.paybridge.application.payments.data.RefundPaymentCommand;
import br.com.aftersunrise.paybridge.application.payments.gateways.PaymentProviderResolver;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.infrastructure.repositories.PaymentRepository;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class RefundPaymentCommandHandler extends CommandHandlerBase<RefundPaymentCommand, PaymentResponse>
        implements IRefundPaymentHandler {

    private static final Logger logger = LoggerFactory.getLogger(RefundPaymentCommandHandler.class);

    private final PaymentRepository paymentRepository;
    private final PaymentProviderResolver paymentProviderResolver;

    public RefundPaymentCommandHandler(
            Validator validator,
            PaymentRepository paymentRepository,
            PaymentProviderResolver paymentProviderResolver) {
        super(logger, validator);
        this.paymentRepository = paymentRepository;
        this.paymentProviderResolver = paymentProviderResolver;
    }

    @Override
    @Transactional
    protected CompletableFuture<HandlerResponseWithResult<PaymentResponse>> doExecute(RefundPaymentCommand request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Payment payment = paymentRepository.findByReferenceId(request.referenceId())
                        .orElseThrow(() -> new BusinessException(
                                "Pagamento não encontrado para referência: " + request.referenceId()));

                var gateway = paymentProviderResolver.resolve(payment.getProvider());
                PaymentResponse providerResponse = gateway.refundPayment(request.referenceId(), request.amount());

                payment.markAsRefunded();
                payment.setStatus(providerResponse.getStatus());
                payment.setUpdatedAt(Instant.now());
                paymentRepository.save(payment);

                providerResponse.setProvider(payment.getProvider());
                return success(providerResponse);

            } catch (BusinessException ex) {
                logger.warn("Erro de negócio ao reembolsar pagamento: {}", ex.getMessage());
                return badRequest("BUSINESS_ERROR", ex.getMessage());
            } catch (Exception ex) {
                logger.error("Erro ao reembolsar pagamento", ex);
                return internalServerError("REFUND_ERROR", "Erro ao reembolsar pagamento");
            }
        });
    }
}
