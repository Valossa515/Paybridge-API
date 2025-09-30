package br.com.aftersunrise.paybridge.application.abstractions.handlers;

import br.com.aftersunrise.paybridge.application.abstractions.data.HandlerResponseWithResult;
import br.com.aftersunrise.paybridge.application.abstractions.data.Message;
import br.com.aftersunrise.paybridge.application.abstractions.data.MessageResources;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IHandler;
import br.com.aftersunrise.paybridge.application.abstractions.interfaces.IQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class QueryHandlerBase<TQuery extends IQuery<TResponse>, TResponse>
        implements IHandler<TQuery, TResponse> {

    private final Validator validator;
    private final Logger logger;

    public QueryHandlerBase(Logger logger, Validator validator) {
        this.validator = validator;
        this.logger = logger;
    }

    @Override
    public CompletableFuture<HandlerResponseWithResult<TResponse>> execute(TQuery request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validate(request);
                return doExecute(request).join();
            } catch (ConstraintViolationException e) {
                logger.warn("Validação falhou para request: {}", request, e);
                HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
                response.setStatusCode(400);
                response.setMessages(
                        e.getConstraintViolations().stream()
                                .map(v -> new Message(v.getPropertyPath().toString(), v.getMessage()))
                                .toList()
                );
                return response;
            } catch (Exception e) {
                logger.error("Erro inesperado ao executar query: {}", request, e);
                return internalServerError("ERR500", MessageResources.get("error.unexpected"));
            }
        });
    }

    protected void validate(TQuery query) {
        Set<ConstraintViolation<TQuery>> violations = validator.validate(query);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    protected abstract CompletableFuture<HandlerResponseWithResult<TResponse>> doExecute(TQuery request);

    protected HandlerResponseWithResult<TResponse> notFound(String message, String s) {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(404);
        response.setMessages(List.of(new Message(MessageResources.get("error.not_found_error_code"), message)));
        return response;
    }

    protected HandlerResponseWithResult<TResponse> success(TResponse result) {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(200);
        response.setResult(result);
        return response;
    }

    protected HandlerResponseWithResult<TResponse> created(TResponse result) {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(201);
        response.setResult(result);
        return response;
    }

    protected HandlerResponseWithResult<TResponse> badRequest(String code, String message) {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(400);
        response.setMessages(List.of(new Message(code, message)));
        return response;
    }

    protected HandlerResponseWithResult<TResponse> noContent() {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(204);
        response.setResult(null);
        return response;
    }

    protected HandlerResponseWithResult<TResponse> unauthorized(String code, String message) {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(401);
        response.setMessages(List.of(new Message(code, message)));
        return response;
    }

    protected HandlerResponseWithResult<TResponse> forbidden(String code, String message) {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(403);
        response.setMessages(List.of(new Message(MessageResources.get("forbidden.error_code"), MessageResources.get("forbidden.error_message"))));
        return response;
    }

    protected HandlerResponseWithResult<TResponse> internalServerError(String code, String message) {
        HandlerResponseWithResult<TResponse> response = new HandlerResponseWithResult<>();
        response.setStatusCode(500);
        response.setMessages(List.of(new Message(code, message)));
        return response;
    }
}
