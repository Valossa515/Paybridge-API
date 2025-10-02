# API Gateway de Pagamentos (PoC)

## 🎯 Objetivo

API agnóstica de pagamentos que unifica chamadas a provedores externos (inicialmente **PicPay** — adapter v2). O objetivo deste PoC é prover um contrato único para criação/consulta/captura/reembolso de pagamentos, encapsulando diferenças de cada gateway através de adaptadores.

## 🧩 Funcionalidades (PoC)

* `POST /payments` — cria um pagamento no provedor selecionado (ex.: PicPay) e persiste a entidade `Payment` local.
* `GET /payments/{referenceId}/status` — consulta o status do pagamento (unificado).
* `POST /payments/{referenceId}/capture` — captura total/parcial (quando aplicável).
* `POST /payments/{referenceId}/refunds` — reembolso parcial ou total.
* `POST /webhook/picpay` — endpoint de callback para notificações do PicPay (mapeado internamente para um fluxo unificado).

## 🔁 Fluxo (resumido)

1. Cliente envia payload unificado para `/payments`.
2. API valida e mapeia para a entidade interna `Payment` (adapter).
3. `PaymentProviderGateway` selecionado (ex.: `PicPayGateway`) realiza a chamada externa.
4. Resposta do provedor é normalizada para `PaymentResponse` via mapper (`PicPayMapper`).
5. Entidade `Payment` é atualizada e salva no repositório local.
6. Notificações/Callbacks são recebidas em `/webhook/*` e resultam em chamadas de confirmação/atualização (GET status no provedor).

## 🧱 Arquitetura e componentes

* **Contract/DTOs**: `Payment`, `PaymentResponse`, `CreatePaymentCommand`.
* **Gateway/Adapter Pattern**: `PaymentProviderGateway` (interface) + `PicPayGateway` (implementação).
* **OAuthService**: gerencia token (cache e refresh simples) para PicPay.
* **Mapper**: `PicPayMapper` — converte resposta do provedor para `PaymentResponse`.
* **Adapter local**: `IPaymentAdapter` para mapear comando → entidade.
* **Persistence**: `PaymentRepository` (JPA) — suporta H2/Postgres.

## ⚙️ Tecnologias

* Java 25
* Spring Boot
* Spring Data JPA
* RestTemplate (pode evoluir para WebClient ou FeignClient)
* Maven
* Banco: PostgreSQL (dev/prod)

## 🔧 Configurações importantes (`application.properties`)

```properties
# PicPay OAuth
picpay.oauth.token-url = https://api.picpay.com/ecommerce/v2/oauth2/token
picpay.oauth.client-id = YOUR_CLIENT_ID
picpay.oauth.client-secret = YOUR_CLIENT_SECRET
picpay.base-url = https://api.picpay.com/ecommerce/v2

# Datasource (exemplo H2)
spring.datasource.url=jdbc:h2:mem:app;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

## 🔬 Exemplos de payload (contrato unificado)

**Request** `POST /payments`

```json
{
  "referenceId": "102030",
  "amount": 20.51,
  "currency": "BRL",
  "callbackUrl": "https://minhaapi.com/webhook/picpay",
  "returnUrl": "https://minhaloja.com/pedido/102030",
  "buyer": {
    "firstName": "João",
    "lastName": "Da Silva",
    "document": "12345678910",
    "email": "teste@teste.com",
    "phone": "+55 27 12345-6789"
  },
  "provider": "picpay"
}
```

**Response** (unificado — `PaymentResponse`)

```json
{
  "referenceId": "102030",
  "status": "PENDING",
  "provider": "picpay",
  "paymentUrl": "https://checkout.picpay.com/..",
  "qrCode": "000201...",
  "amount": 20.51
}
```

## ▶️ Como rodar localmente

1. Preencher `application.properties` com `picpay.oauth.client-id` e `client-secret` (ou usar valores mock).
2. `mvn clean install`
3. `mvn spring-boot:run`
4. Testar endpoints via Postman / Insomnia.

## 📌 Próximos passos

* Implementar `Resilience4j` (retry/circuit-breaker) nas chamadas aos provedores.
* Adicionar testes de integração com mock de provider (WireMock).
* Plugar cache distribuído (Redis) e mensageria (Kafka) quando necessário.
* Documentar com OpenAPI/Swagger.

## 📝 Observações

Este repositório é um PoC/serviço interno para unificação de integrações com gateways de pagamento — não é, por si só, um PSP regulado. Para operar com dinheiro real em produção em escala, é necessário atender requisitos regulatórios e contratuais dos provedores e autoridades.

---
