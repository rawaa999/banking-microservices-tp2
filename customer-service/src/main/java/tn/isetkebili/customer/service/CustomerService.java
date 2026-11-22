package tn.isetkebili.customer.service;

import org.springframework.stereotype.Service;
import tn.isetkebili.customer.entity.Customer;
import tn.isetkebili.customer.exception.ResourceNotFoundException;
import tn.isetkebili.customer.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer create(Customer customer) {
        repository.findByEmail(customer.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("Un client avec cet email existe déjà : " + customer.getEmail());
        });
        return repository.save(customer);
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'ID : " + id));
    }

    public Customer update(Long id, Customer updated) {
        Customer existing = findById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Customer existing = findById(id);
        repository.delete(existing);
    }
}