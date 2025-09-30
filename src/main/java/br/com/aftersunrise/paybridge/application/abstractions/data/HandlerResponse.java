package br.com.aftersunrise.paybridge.application.abstractions.data;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class HandlerResponse {
    private int statusCode;
    private List<Message> messages = Collections.emptyList();
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode <= 299;
    }
}