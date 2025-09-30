package br.com.aftersunrise.paybridge.domain.models.notifications;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Embeddable
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification implements Serializable {
    private Boolean disablePush;
    private Boolean disableEmail;
}
