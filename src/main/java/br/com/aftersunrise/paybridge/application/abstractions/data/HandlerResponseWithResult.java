package br.com.aftersunrise.paybridge.application.abstractions.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HandlerResponseWithResult<T> extends HandlerResponse {
    private T result;

    @Override
    public String toString() {
        return "HandlerResponseWithResult{" +
                "statusCode=" + getStatusCode() +
                ", messages=" + getMessages() +
                ", result=" + result +
                '}';
    }
}
