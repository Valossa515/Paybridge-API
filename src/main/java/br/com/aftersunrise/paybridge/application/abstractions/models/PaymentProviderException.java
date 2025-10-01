package br.com.aftersunrise.paybridge.application.abstractions.models;

public class PaymentProviderException extends RuntimeException{
    public PaymentProviderException(String message) { super(message); }
    public PaymentProviderException(String message, Throwable cause) { super(message, cause); }
}
