package br.com.aftersunrise.paybridge.application.payments.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PicPayPaymentResponse {
    private String referenceId;
    private String status;
    private String paymentUrl;
    private PicPayQRCode qrcode;
}
