# OrionPay Interchange Visa

Este microsserviço é responsável pelo processamento de arquivos de liquidação (Settlement) e intercâmbio da rede Visa (especificamente os arquivos TC50/VSS). Ele atua como o motor de conciliação financeira entre as liquidações enviadas pela bandeira Visa e as autorizações processadas internamente pela OrionPay.

O sistema foi desenhado com foco em resiliência, rastreabilidade financeira e alto desempenho no processamento de lotes.

## Arquitetura

O projeto adota rigorosamente os princípios da **Arquitetura Hexagonal (Ports and Adapters)** combinada com os conceitos de **Domain-Driven Design (DDD)**. Esta abordagem garante que as regras de negócio complexas do intercâmbio Visa fiquem completamente isoladas de detalhes tecnológicos como banco de dados, frameworks (Spring) ou formatos de arquivos.

Para a orquestração do processamento dos arquivos, utilizamos o **Spring Batch**.

### Estrutura Multi-Módulo

O projeto está dividido em três módulos principais. Clique nos links abaixo para ver os detalhes técnicos e regras de negócio de cada camada:

1. **[Core / Domínio (`orionpay-domain-visa`)](./orionpay-domain-visa/README.md)**
   - O coração do sistema. Contém os modelos ricos, Value Objects, casos de uso e as interfaces (Ports) que ditam como o sistema interage com o mundo exterior. Não possui dependências do Spring ou JPA.

2. **[Infraestrutura (`orionpay-infra-visa`)](./orionpay-infra-visa/README.md)**
   - A camada tecnológica. Implementa as portas definidas pelo domínio. Contém as Entidades JPA, repositórios do Spring Data, Mappers (MapStruct) e Adapters de comunicação externa.

3. **[Aplicação / Incoming (`orionpay-incoming-visa`)](./orionpay-incoming-visa/README.md)**
   - A porta de entrada do sistema. Contém a configuração do Spring Batch, Tasklets, ItemReaders para parsing de arquivos de texto posicional (Fixed-Length) e Controllers REST para disparar os Jobs.

## Fluxo de Processamento (Pipeline Spring Batch)

O processamento de um arquivo Visa TC50 segue o seguinte pipeline orquestrado pelo módulo `incoming`:

1. **Inicialização do Job**: O `VisaJobListener` cria um registro de controle na tabela `visa_file_control` com status `PROCESSING` e gera um UUID de correlação.
2. **Leitura do Cabeçalho (`ReadVisaHeaderStep`)**: Lê a primeira linha (TCR90), valida o arquivo e extrai o BIN de origem e a data de transmissão.
3. **Processamento de Transações (`VisaSettlementStep`)**: 
   - Faz o parsing posicional (Fixed-Length) das linhas TC50 TCR0.
   - Aplica regras de domínio, salva as transações como `PENDING` na base de dados.
   - Registra cada linha na tabela de auditoria `audit_log_base`.
   - Utiliza política de *Skip* para não interromper o lote caso uma linha esteja corrompida.
4. **Conciliação Financeira (`ReconciliationStep`)**: 
   - Busca as transações `PENDING` e aciona o serviço de domínio para casar (Match) com as transações internas da OrionPay.
   - Atualiza o status para `MATCHED`, `DISPUTED` ou `UNMATCHED_VISA` e grava o histórico de auditoria.
5. **Finalização do Job**: Atualiza o registro de controle do arquivo com o total de registros processados, o valor financeiro consolidado e altera o status para `COMPLETED` ou `FAILED`.

## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.x
- Spring Batch
- Spring Data JPA
- PostgreSQL
- MapStruct (Para mapeamento DTO -> Domínio -> Entity)
- Lombok

## Como Executar

O projeto é empacotado através do módulo raiz (que atua apenas como agregador POM).

```bash
# Para compilar e gerar os JARs de todos os módulos
mvn clean install

# Para executar a aplicação (O entrypoint está no módulo incoming, mas o Spring Boot plugin pode estar configurado no parent ou no incoming)
# A partir do diretório raiz:
java -jar orionpay-incoming-visa/target/orionpay-incoming-visa-0.0.1-SNAPSHOT.jar
```

Para iniciar um Job de processamento de arquivo:
```bash
curl -X POST "http://localhost:8080/api/visa/jobs/settlement/start?fileName=caminho/para/o/arquivo/TC50.txt"
```
