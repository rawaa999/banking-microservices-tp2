# banking-microservices-tp2

Architecture microservices bancaires en **Spring Boot**, **PostgreSQL** et **Docker**, connectée à l'application Android **MyPremiereApplication-TP2**.

## Services et ports
- **Customer Service** : port `8081` (`/api/customers`)
- **Account Service** : port `8082` (`/api/accounts`)
- **Transaction Service** : port `8083` (`/api/transactions`)
- **Notification Service** : port `8084` (`/api/notifications`)

## Démarrage
```bash
docker compose up -d
