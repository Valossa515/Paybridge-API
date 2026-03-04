package br.com.aftersunrise.paybridge.infrastructure.repositories;

import br.com.aftersunrise.paybridge.domain.models.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByReferenceId(String referenceId);
}