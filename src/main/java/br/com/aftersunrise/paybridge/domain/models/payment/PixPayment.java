package br.com.aftersunrise.paybridge.domain.models.payment;


import br.com.aftersunrise.paybridge.domain.models.DatabaseEntityBase;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "pix_payment")
@Builder
@Entity
public class PixPayment extends DatabaseEntityBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    private String pixKey;
    private String qrCode;
    private Instant expiresAt;
}
