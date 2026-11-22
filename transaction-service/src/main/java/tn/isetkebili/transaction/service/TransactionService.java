package tn.isetkebili.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tn.isetkebili.transaction.entity.Transaction;
import tn.isetkebili.transaction.entity.TransactionStatus;
import tn.isetkebili.transaction.entity.TransactionType;
import tn.isetkebili.transaction.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;
    private final RestClient accountRestClient;
    private final RestClient notificationRestClient;

    public TransactionService(TransactionRepository repository,
                              RestClient accountRestClient,
                              RestClient notificationRestClient) {
        this.repository = repository;
        this.accountRestClient = accountRestClient;
        this.notificationRestClient = notificationRestClient;
    }

    public Transaction execute(Transaction tx) {
        tx.setCreatedAt(LocalDateTime.now());

        try {
            if (tx.getType() == TransactionType.DEPOSIT) {
                if (tx.getTargetAccountId() == null) {
                    throw new IllegalArgumentException("targetAccountId est obligatoire pour un dépôt");
                }
                creditAccount(tx.getTargetAccountId(), tx);
                tx.setStatus(TransactionStatus.SUCCESS);
                Transaction saved = repository.save(tx);
                notifyCustomer(tx.getTargetAccountId(),
                        "Dépôt de " + tx.getAmount() + " TND effectué sur le compte " + tx.getTargetAccountId());
                return saved;

            } else if (tx.getType() == TransactionType.WITHDRAWAL) {
                if (tx.getSourceAccountId() == null) {
                    throw new IllegalArgumentException("sourceAccountId est obligatoire pour un retrait");
                }
                debitAccount(tx.getSourceAccountId(), tx);
                tx.setStatus(TransactionStatus.SUCCESS);
                Transaction saved = repository.save(tx);
                notifyCustomer(tx.getSourceAccountId(),
                        "Retrait de " + tx.getAmount() + " TND effectué sur le compte " + tx.getSourceAccountId());
                return saved;

            } else if (tx.getType() == TransactionType.TRANSFER) {
                if (tx.getSourceAccountId() == null || tx.getTargetAccountId() == null) {
                    throw new IllegalArgumentException("sourceAccountId et targetAccountId sont obligatoires pour un virement");
                }
                if (tx.getSourceAccountId().equals(tx.getTargetAccountId())) {
                    throw new IllegalArgumentException("Les comptes source et destination doivent être différents");
                }
                debitAccount(tx.getSourceAccountId(), tx);
                try {
                    creditAccount(tx.getTargetAccountId(), tx);
                } catch (Exception e) {
                    // Compensation en cas d'échec du crédit
                    creditAccount(tx.getSourceAccountId(), tx);
                    throw e;
                }
                tx.setStatus(TransactionStatus.SUCCESS);
                Transaction saved = repository.save(tx);
                notifyCustomer(tx.getSourceAccountId(),
                        "Virement émis de " + tx.getAmount() + " TND vers le compte " + tx.getTargetAccountId());
                notifyCustomer(tx.getTargetAccountId(),
                        "Virement reçu de " + tx.getAmount() + " TND depuis le compte " + tx.getSourceAccountId());
                return saved;
            }
            throw new IllegalArgumentException("Type de transaction invalide");

        } catch (RestClientResponseException ex) {
            tx.setStatus(TransactionStatus.FAILED);
            repository.save(tx);
            throw new IllegalStateException("Échec de l'opération sur le compte : " + ex.getResponseBodyAsString());
        } catch (RuntimeException ex) {
            tx.setStatus(TransactionStatus.FAILED);
            repository.save(tx);
            throw ex;
        }
    }

    private void debitAccount(Long accountId, Transaction tx) {
        accountRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/accounts/{id}/debit")
                        .queryParam("amount", tx.getAmount())
                        .build(accountId))
                .retrieve()
                .toBodilessEntity();
    }

    private void creditAccount(Long accountId, Transaction tx) {
        accountRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/accounts/{id}/credit")
                        .queryParam("amount", tx.getAmount())
                        .build(accountId))
                .retrieve()
                .toBodilessEntity();
    }

    private void notifyCustomer(Long accountId, String message) {
        try {
            Map<String, Object> account = accountRestClient.get()
                    .uri("/api/accounts/{id}", accountId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (account != null && account.get("customerId") != null) {
                Long customerId = Long.valueOf(account.get("customerId").toString());
                notificationRestClient.post()
                        .uri("/api/notifications")
                        .body(Map.of("customerId", customerId, "message", message))
                        .retrieve()
                        .toBodilessEntity();
            }
        } catch (Exception e) {
            log.warn("Impossible d'envoyer la notification pour le compte {} : {}", accountId, e.getMessage());
        }
    }

    public List<Transaction> findAll() {
        return repository.findAll();
    }

    public List<Transaction> findByAccountId(Long accountId) {
        return repository.findBySourceAccountIdOrTargetAccountId(accountId, accountId);
    }
}