package br.com.aftersunrise.paybridge.application.payments.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PicPayQRCode {
    private String content;
    private String base64;
}
