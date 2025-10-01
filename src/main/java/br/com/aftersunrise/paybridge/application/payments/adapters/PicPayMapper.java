package br.com.aftersunrise.paybridge.application.payments.adapters;

import br.com.aftersunrise.paybridge.application.payments.data.PaymentResponse;
import br.com.aftersunrise.paybridge.application.payments.data.PicPayPaymentResponse;
import br.com.aftersunrise.paybridge.domain.models.payment.enums.PaymentStatus;

public class PicPayMapper {
    public static PaymentResponse toPaymentResponse(PicPayPaymentResponse resp) {
        PaymentResponse pr = new PaymentResponse();
        pr.setReferenceId(resp.getReferenceId());
        pr.setStatus(toPaymentStatus(resp.getStatus()));
        pr.setPaymentUrl(resp.getPaymentUrl());
        if (resp.getQrcode() != null) {
            pr.setQrCode(resp.getQrcode().getContent());
            pr.setQrCodeBase64(resp.getQrcode().getBase64());
        }
        return pr;
    }

    public static PaymentStatus toPaymentStatus(String picPayStatus) {
        return switch (picPayStatus.toLowerCase()) {
            case "paid" -> PaymentStatus.CONFIRMED;
            case "expired" -> PaymentStatus.FAILED;
            case "refunded" -> PaymentStatus.REFUNDED;
            case "pending" -> PaymentStatus.PENDING;
            default -> PaymentStatus.FAILED;
        };
    }
}
