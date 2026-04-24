package orionpay.interchange.visa.incoming.application.batch.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.usecase.ProcessVisaSettlementUseCase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.batch.context.VisaProcessContext;
import orionpay.interchange.visa.incoming.application.dto.TC50TCR0Dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisaTc50ItemProcessor implements ItemProcessor<TC50TCR0Dto, VisaSettlementMaster> {

    private final ProcessVisaSettlementUseCase processVisaSettlementUseCase;
    private final VisaProcessContext visaProcessContext;

    @Override
    public VisaSettlementMaster process(TC50TCR0Dto lineDto) throws Exception {

        log.info(">>>> Processor recebeu transação: TC={}, Valor={}",
                lineDto.getTransactionCode(), lineDto.getAmount());

        String tc = lineDto.getTransactionCode();
        if ("90".equals(tc) || "92".equals(tc)) {
            return null;
        }

        // PROCESSAR APENAS TRANSAÇÕES (00 OU 50)
        if (!"00".equals(tc) && !"50".equals(tc)) {
            return null;
        }

        // Apenas processa liquidações TC50
        // Altere para aceitar "00" (padrão de muitos arquivos TCR0) ou o código do seu teste
        if (!"00".equals(lineDto.getTransactionCode()) && !"50".equals(lineDto.getTransactionCode())) {
            log.debug("Ignorando registro com Transaction Code: {}", lineDto.getTransactionCode());
            return null;
        }
        try {
            CorrelationId correlationId = new CorrelationId(visaProcessContext.getCorrelationId());

            // 1. Parsing do Valor (Assume-se que os últimos 2 dígitos são decimais)
            BigDecimal rawAmountValue = new BigDecimal(lineDto.getAmount().trim()).divide(new BigDecimal(100));
            MoneyAmount grossAmount = MoneyAmount.of(rawAmountValue);

            // No TC50 (TCR0), a taxa de intercâmbio pode não vir na mesma linha
            MoneyAmount interchangeFee = MoneyAmount.of(BigDecimal.ZERO);

            // 2. Parsing da Data (Formato MMDD)
            LocalDate settlementDate = parseVisaDate(lineDto.getSettlementDate());

            // 3. Construção do Domínio Rico
            // 3. Construção do Domínio Rico
            VisaSettlementMaster settlementMaster = new VisaSettlementMaster(
                    correlationId,
                    lineDto.getDestinationBin().trim(),
                    grossAmount,
                    interchangeFee,
                    settlementDate,
                    lineDto.getUsageCode() != null ? lineDto.getUsageCode().trim() : "",
                    lineDto.getReasonCode() != null ? lineDto.getReasonCode().trim() : ""
            );

            settlementMaster.setTransactionIdOrionpay(UUID.randomUUID().toString());
            settlementMaster.setAuthCode(UUID.randomUUID().toString().substring(0, 6));

            // 4. Executa a orquestração do Caso de Uso, passando a linha bruta (rawLine)
            // O Use Case irá salvar a auditoria e a transação em si e nos retornará o objeto salvo.
            VisaSettlementMaster processedSettlement = processVisaSettlementUseCase.execute(settlementMaster, lineDto.getRawLine());

            // 5. Atualiza os totais no contexto *apenas* após o UseCase aprovar e não lançar exceção
            visaProcessContext.incrementTotalRecordCount();
            visaProcessContext.addTotalAmount(rawAmountValue);

            return processedSettlement;

        } catch (Exception e) {
            log.error("Erro ao processar a linha do arquivo TC50. Erro: {}", e.getMessage());
            // Lançar a exceção fará o Spring Batch acionar a política de Skip (se configurada)
            throw e;
        }
    }

    private LocalDate parseVisaDate(String mmdd) {
        if (mmdd == null || mmdd.trim().length() < 4) {
            return LocalDate.now(); // Fallback
        }
        int currentYear = LocalDate.now().getYear();
        String dateString = currentYear + "-" + mmdd.substring(0, 2) + "-" + mmdd.substring(2, 4);
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            log.warn("Falha ao analisar data Visa: {}. Usando data atual.", mmdd);
            return LocalDate.now();
        }
    }
}
