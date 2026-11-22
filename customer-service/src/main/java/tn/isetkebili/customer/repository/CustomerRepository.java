package tn.isetkebili.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.isetkebili.customer.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}