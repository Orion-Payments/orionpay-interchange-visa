package orionpay.interchange.visa.incoming.application.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;

@Slf4j
@Component
public class NoOpItemWriter implements ItemWriter<VisaSettlementMaster> {

    @Override
    public void write(Chunk<? extends VisaSettlementMaster> chunk) {
        // O Domain Service (ProcessVisaSettlementUseCase) já persiste o dado durante a fase de Processing.
        // Isso foi feito para garantir que a auditoria e a liquidação sejam criadas em transações atômicas de negócio no nível do domínio.
        // O ItemWriter aqui atua apenas como um finalizador de log para o chunk.
        log.info("Lote (Chunk) processado com {} transações.", chunk.size());
    }
}
