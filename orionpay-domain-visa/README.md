# OrionPay Domain Visa

Este é o módulo principal (Core) da aplicação de liquidação da rede Visa na OrionPay.
Ele define a lógica de negócio, entidades ricas, regras de validação financeira e a orquestração do sistema.

## Propósito da Camada de Domínio

Seguindo o padrão de **Arquitetura Hexagonal (Ports and Adapters)**, a camada de Domínio **não deve ter qualquer dependência tecnológica externa**.
- Não contém anotações do framework Spring (`@Service`, `@Component`).
- Não contém bibliotecas de base de dados (`JPA`, `Hibernate`, `@Entity`, `@Table`).
- Não conhece formatos de arquivos externos (como `Fixed-Length` da Visa ou `JSON`).

O domínio apenas expõe **Ports** (interfaces de entrada/saída) para que os adaptadores externos (como um controlador REST ou um repositório JPA) se integrem com as suas regras.

## Principais Componentes do Domínio

### 1. Modelos (Domain Models)

Objetos de negócio focados em regras e não apenas contentores de dados (classes anémicas).
Eles protegem o próprio estado.

- **`VisaSettlementMaster`**: Representa uma transação TC50 processada e liquidada pela Visa. Ele inicia sempre o seu estado como `PENDING`.
  - Regras: `calculateNetAmount()` retorna o valor bruto subtraído da taxa de intercâmbio (Interchange Fee).
  - Pode ter o seu estado de conciliação modificado através de `changeReconciliationStatus()`.

- **`AuditLogBase`**: Registo de auditoria imutável criado antes da gravação de qualquer transação na base de dados para garantir que não se perdem dados (a linha `raw` completa).

- **`VisaFileControl`**: Representa o arquivo TC50/VSS da Visa no seu ciclo de vida inteiro (`PROCESSING`, `COMPLETED`, `FAILED`).

### 2. Value Objects (VOs)

Classes imutáveis que evitam a "obsessão por tipos primitivos", validando a si próprias no momento da criação.

- **`MoneyAmount`**: Garante que valores monetários não sejam nulos e que tenham uma moeda associada (default para `986` - BRL).
- **`CorrelationId`**: Encapsula o UUID do lote/arquivo, lançando exceção se estiver em branco ou nulo, o que seria catastrófico para a auditoria do *batch*.

### 3. Casos de Uso e Serviços (Use Cases / Domain Services)

Responsáveis pela orquestração das entidades e Value Objects.

- **`ProcessVisaSettlementUseCase`**: Coordena o registo inicial da linha na auditoria (status PROCESSING), grava a liquidação (`VisaSettlementMaster`) através de uma Porta, e atualiza a auditoria final (status sucesso/falha) numa transação atómica em relação ao domínio.
  - *Regra: Lança `IllegalArgumentException` se o valor bruto (`grossAmount`) for menor ou igual a zero.*

- **`FinancialReconciliationService`**: O motor de *Match* de transações.
  - Verifica se o `RRN` e o `AuthCode` estão presentes na liquidação enviada pela Visa.
  - Consulta as Autorizações Internas da OrionPay através do `InternalTransactionPort`.
  - **Regras de Match**:
    - Se não encontrar a transação interna: Status -> `UNMATCHED_VISA`.
    - Se encontrar, mas com valor divergente do liquidado: Status -> `DISPUTED`.
    - Se tudo casar perfeitamente: Status -> `MATCHED`.
  - Este serviço também emite um registo de auditoria `VisaReconciliationHistory` para cada mudança de status do dinheiro.

### 4. Ports (Portas de Saída / Interfaces)

Interfaces que declaram contratos que as camadas externas (Infraestrutura) devem implementar para que o Domínio possa armazenar e recuperar dados.

- `VisaSettlementRepositoryPort`
- `InternalTransactionPort`
- `AuditLogBaseRepositoryPort`
- `VisaReconciliationHistoryPort`
- `VisaFileControlRepositoryPort`
