package br.com.aftersunrise.paybridge.domain.models.customer;

import br.com.aftersunrise.paybridge.domain.models.DatabaseEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_info")
@Builder
@Entity
public class CustomerInfo extends DatabaseEntityBase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String documentNumber;
}