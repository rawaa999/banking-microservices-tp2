package tn.isetkebili.transaction.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.isetkebili.transaction.entity.Transaction;
import tn.isetkebili.transaction.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction execute(@Valid @RequestBody Transaction transaction) {
        return service.execute(transaction);
    }

    @GetMapping
    public List<Transaction> findAll() {
        return service.findAll();
    }

    @GetMapping("/account/{accountId}")
    public List<Transaction> findByAccount(@PathVariable Long accountId) {
        return service.findByAccountId(accountId);
    }
}