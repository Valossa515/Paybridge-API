package br.com.aftersunrise.paybridge.domain.services.payments;

import br.com.aftersunrise.paybridge.application.abstractions.data.HandlerResponseWithResult;
import br.com.aftersunrise.paybridge.application.abstractions.handlers.CommandHandlerBase;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.ICreatePaymentHandler;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IPaymentAdapter;
import br.com.aftersunrise.paybridge.application.abstractions.models.BusinessException;
import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentCommand;
import br.com.aftersunrise.paybridge.application.payments.data.CreatePaymentResponse;
import br.com.aftersunrise.paybridge.application.payments.data.PaymentResponse;
import br.com.aftersunrise.paybridge.application.payments.gateways.PaymentProviderResolver;
import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;
import br.com.aftersunrise.paybridge.infrastructure.repositories.PaymentRepository;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class CreatePaymentCommandHandler extends CommandHandlerBase<CreatePaymentCommand, CreatePaymentResponse>
    implements ICreatePaymentHandler {

    private static final Logger logger = LoggerFactory.getLogger(CreatePaymentCommandHandler.class);

    private final PaymentRepository paymentRepository;
    private final PaymentProviderResolver paymentProviderResolver;
    private final IPaymentAdapter paymentAdapter;

    public CreatePaymentCommandHandler(
            Validator validator,
            PaymentRepository paymentRepository,
            PaymentProviderResolver paymentProviderResolver,
            IPaymentAdapter paymentAdapter) {
        super(logger, validator);
        this.paymentRepository = paymentRepository;
        this.paymentProviderResolver = paymentProviderResolver;
        this.paymentAdapter = paymentAdapter;
    }

    @Override
    @Transactional
    protected CompletableFuture<HandlerResponseWithResult<CreatePaymentResponse>> doExecute(CreatePaymentCommand request) {
        return CompletableFuture.supplyAsync(() -> {

           try{
               // 1. Monta entidade Payment a partir do command
               Payment payment = paymentAdapter.toPayment(request);
               var gateway = paymentProviderResolver.resolve(payment.getProvider());
               payment.setProvider(gateway.getProviderId());
               payment.setStatus(PaymentStatus.PENDING);
               payment.setCreatedAt(Instant.now());

               // 2. Cria pagamento no provedor (PicPay)
               PaymentResponse providerResponse = gateway.createPayment(payment);

               // 3. Atualiza entidade local com dados do provedor
               payment.setReferenceId(providerResponse.getReferenceId());
               payment.setStatus(providerResponse.getStatus());
               String resolvedProvider = providerResponse.getProvider() != null
                       ? providerResponse.getProvider()
                       : gateway.getProviderId();
               payment.setProvider(resolvedProvider);
               payment.setUpdatedAt(Instant.now());

               // 4. Persiste no banco
               Payment saved = paymentRepository.save(payment);

               // 5. Monta resposta DTO
               CreatePaymentResponse result = new CreatePaymentResponse(
                       saved.getReferenceId(),
                       saved.getStatus(),
                       saved.getProvider(),
                       providerResponse.getPaymentUrl(),
                       providerResponse.getQrCode(),
                       providerResponse.getQrCodeBase64()
               );

               return success(result);

           }
           catch (BusinessException ex){
                logger.warn("Erro de negócio ao criar pagamento: {}", ex.getMessage());
                return badRequest("BUSINESS_ERROR", ex.getMessage());
           }
           catch (Exception ex){
               logger.error("Erro ao criar pagamento", ex);
               return badRequest("PAYMENT_ERROR", "Erro ao criar pagamento");
           }
        });
    }
}