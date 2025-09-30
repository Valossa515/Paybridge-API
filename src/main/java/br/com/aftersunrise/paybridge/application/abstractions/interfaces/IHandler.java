package br.com.aftersunrise.paybridge.application.abstractions.interfaces;

import br.com.aftersunrise.paybridge.application.abstractions.data.HandlerResponseWithResult;

import java.util.concurrent.CompletableFuture;

public interface IHandler<TInput, TOutput> {
    CompletableFuture<HandlerResponseWithResult<TOutput>> execute(TInput request);
}