# OrionPay Infra Visa

Este é o módulo de Infraestrutura (Adapters) do projeto de liquidação da rede Visa.
O papel deste módulo é conectar o Core de Negócio (módulo `orionpay-domain-visa`) com tecnologias externas, neste caso, o banco de dados PostgreSQL.

## Arquitetura Hexagonal: A Camada de Infraestrutura

Na Arquitetura Hexagonal, a camada de infraestrutura não dita regras. Ela é apenas um "plug-in" que satisfaz os contratos (Ports) definidos pelo Domínio.

Se decidirmos trocar o banco de dados de PostgreSQL para MongoDB amanhã, apenas este módulo (`orionpay-infra-visa`) será reescrito. O domínio permanecerá intocado.

### Responsabilidades

1. **Persistência de Dados (JPA/Hibernate)**
   - Mapeamento objeto-relacional das tabelas de intercâmbio da Visa (`visa_settlement_master`, `audit_log_base`, `visa_file_control`, `visa_reconciliation_history`, etc.).
   - Utilização do Spring Data JPA (`JpaRepository`) para operações CRUD.
   - Gerenciamento de chaves primárias e campos de auditoria base (`created_at`, `updated_at`, `uuid`).

2. **Integração com APIs Externas**
   - Implementação de clientes HTTP/REST para buscar dados em outros microsserviços. Exemplo: `InternalTransactionAdapter`, que (em um cenário produtivo) consultaria o serviço de Autorização da OrionPay para realizar a conciliação.

3. **Mapeamento DTO <-> Entity <-> Domain**
   - Utiliza a biblioteca **MapStruct** para traduzir as Entidades JPA anêmicas para os Modelos de Domínio Ricos (e vice-versa).
   - O MapStruct foi configurado com métodos `@Named` para lidar com a injeção e extração de Value Objects, como `CorrelationId` e `MoneyAmount`. Exemplo: Desempacotar um `MoneyAmount` do domínio para gravar apenas o `BigDecimal` no banco.

## Principais Componentes

### Adapters (Implementação das Portas)
As classes anotadas com `@Component` que implementam as interfaces do módulo `orionpay-domain-visa`.

- **`VisaSettlementRepositoryAdapter`**: Grava as transações TC50 no banco.
- **`AuditLogBaseRepositoryAdapter`**: Grava os logs brutos (raw data) do arquivo posicional da Visa para garantir rastreabilidade.
- **`VisaFileControlRepositoryAdapter`**: Gerencia o estado do lote/arquivo (PROCESSING, COMPLETED, FAILED) e os seus totais financeiros.
- **`VisaReconciliationHistoryAdapter`**: Persiste o histórico de mudanças de status (de PENDING para MATCHED, por exemplo) durante o processo de conciliação de caixa.
- **`InternalTransactionAdapter`**: Busca transações autorizadas pela OrionPay para comparar com o arquivo de liquidação da Visa. Atualmente, possui um mock baseado em `RRN` para simular os fluxos de sucesso e erro na conciliação.

### Entidades (Entities)
Classes que espelham o DDL do banco de dados (schema `visa_interchange`).
Eles utilizam Lombok para getters/setters e a classe base abstrata (`VisaBaseEntity`) para padronizar UUIDs e datas de criação via `@PrePersist`.
