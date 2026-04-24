# OrionPay Incoming Visa

Este é o módulo de Aplicação (Incoming) do projeto de liquidação da rede Visa.
O papel deste módulo é expor as interfaces para o mundo exterior e orquestrar a execução das regras de negócio (Domínio) usando o **Spring Batch**.

## A Camada de Aplicação (Incoming / Primary Adapters)

Na Arquitetura Hexagonal, esta camada é responsável por traduzir a entrada externa (uma chamada HTTP REST ou a leitura de um arquivo de texto) num formato que o domínio entende (Modelos e Casos de Uso), invocando a lógica de negócio principal.

Não contém regras de negócio. O seu único objetivo é orquestrar o motor de processamento em lote.

## Principais Componentes e Fluxo de Lote

### 1. Controladores REST (Job Launcher)
Apenas expõe APIs para os utilizadores/sistemas dispararem os Jobs do Spring Batch assincronamente.
- **`JobLauncherController`**: O endpoint `/api/visa/jobs/settlement/start` recebe o caminho do arquivo TC50 e aciona o Job de Liquidação (`visaSettlementJob`).

### 2. O Motor: Spring Batch (`VisaSettlementJobConfig`)
A configuração central do Pipeline de processamento que segue os seguintes passos:

1. **Listener (`VisaJobListener`)**: Inicia o arquivo na tabela de controle de arquivos (status `PROCESSING`), cria o UUID do lote e prepara o contexto da thread (`VisaProcessContext`).
2. **Step 1: Cabeçalho (`readVisaHeaderStep`)**: Lê apenas a primeira linha do arquivo (TCR90), extrai o BIN de origem, data de transmissão e atualiza imediatamente o banco de dados. Se o cabeçalho for inválido, interrompe o Job sem ler milhões de registros desnecessariamente.
3. **Step 2: Transações (`visaSettlementStep`)**: Lê o corpo do arquivo (TC50 TCR0), usando uma estratégia `Skip` tolerante a falhas linha a linha. Acumula totais financeiros na memória da thread.
4. **Step 3: Conciliação Financeira (`reconciliationStep`)**: Executa uma nova tasklet que consulta a base para realizar o "Match" financeiro de todas as transações `PENDING` do lote com as autorizações internas, liberando o dinheiro para o caixa da OrionPay (`MATCHED`).
5. **Listener Final (`VisaJobListener`)**: Atualiza o registro de controle do arquivo com a quantidade de transações e valor bruto (Total Amount) do lote inteiro. Fecha o status para `COMPLETED` ou `FAILED`.

### 3. O Leitor Posicional (Fixed-Length Reader)
O coração da extração de dados do Layout Visa VSS.
- **`VisaTc50ItemReader`**: Define um `FlatFileItemReader` com um `FixedLengthTokenizer` configurado exatamente para os *Ranges* do manual da Visa (Exemplo: posições 31 a 42 para o valor monetário, 75 a 78 para a data MMDD).
- **Flexibilidade**: Usa a flag `strict=false` para tolerar variações mínimas de espaços em branco ou quebras de linha (`\n` vs `\r\n`) de sistemas de mainframe.

### 4. Tasklets (Orquestradores de Regra)
- **`ReadVisaHeaderTasklet`**: Lê o registro TCR90 (cabeçalho).
- **`VisaSettlementTasklet`**: Utiliza o leitor posicional acima para varrer as linhas (ignora o que não for transação 50). Aplica a transformação de strings para valores decimais (`BigDecimal / 100`) e invoca o `ProcessVisaSettlementUseCase` no domínio. Se uma linha vier corrompida, ela é pulada e logada sem afetar as outras.
- **`ReconciliationTasklet`**: Busca transações `PENDING` e chama o `FinancialReconciliationService`.

### 5. Contexto de Thread Seguro
- **`VisaProcessContext`**: Uma classe anotada com `@JobScope` que atua como memória compartilhada durante a execução do Job. É `thread-safe` e armazena totais cumulativos de dinheiro (`AtomicInteger`, `BigDecimal` sincronizado) garantindo que, mesmo se o Batch for particionado (Multi-threading), o saldo final do arquivo será exato.

### 6. Mappers de Aplicação (AppMapper)
Uso do MapStruct para mapear do DTO de Entrada (`TC50TCR0Dto` que possui todos os campos como String diretamente do texto) para o Modelo de Domínio `VisaSettlementMaster` com os seus Value Objects.
