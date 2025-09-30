package br.com.aftersunrise.paybridge.application.abstractions.data;

import org.springframework.stereotype.Component;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
public class MessageResources {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }
}