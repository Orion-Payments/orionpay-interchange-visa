package orionpay.interchange.visa.infrastructure.adapters.output.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.InternalTransaction;
import orionpay.interchange.visa.domain.ports.output.InternalTransactionPort;
import orionpay.interchange.visa.domain.vo.MoneyAmount;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Adapter para buscar a transação interna.
 * Em um cenário real, usaria RestTemplate/FeignClient para chamar o Merchant Service,
 * ou consultaria uma tabela compartilhada / View no banco de dados.
 * Aqui, mockamos o comportamento para fins de arquitetura.
 */
@Slf4j
@Component
public class InternalTransactionAdapter implements InternalTransactionPort {

    @Override
    public Optional<InternalTransaction> findByRrnAndAuthCode(String rrn, String authCode) {
        log.info("Buscando transação interna OrionPay com RRN: {} e AuthCode: {}", rrn, authCode);

        // Simulando a lógica de conciliação:
        // 1. Se o RRN for "000000000000", simulamos que não foi encontrada (UNMATCHED_VISA)
        if ("000000000000".equals(rrn)) {
            return Optional.empty();
        }

        // 2. Se o RRN for "999999999999", simulamos uma divergência de valor (DISPUTED)
        if ("999999999999".equals(rrn)) {
            return Optional.of(InternalTransaction.builder()
                    .rrn(rrn)
                    .authCode(authCode)
                    .authorizedAmount(MoneyAmount.of(new BigDecimal("100.00"))) // Valor diferente do TC50 simulado (150.00)
                    .status("AUTHORIZED")
                    .build());
        }

        // 3. Caso padrão: transação encontrada com o mesmo valor esperado no TC50 tasklet mockado (150.00)
        return Optional.of(InternalTransaction.builder()
                .rrn(rrn)
                .authCode(authCode)
                .authorizedAmount(MoneyAmount.of(new BigDecimal("150.00"))) // MATCHED
                .status("CAPTURED")
                .build());
    }
}
