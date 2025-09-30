package br.com.aftersunrise.paybridge.domain.models.payment;

import br.com.aftersunrise.paybridge.domain.models.DatabaseEntityBase;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "pix_payment")
@Data
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PixPayment extends DatabaseEntityBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    private String qrCodeContent;   // qrcode.content
    private String qrCodeBase64;    // qrcode.base64
    private String paymentUrl;      // checkout URL do PicPay
}
