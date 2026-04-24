package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.InternalTransaction;

import java.util.Optional;

/**
 * Interface que esconde a origem dos dados internos (API ou Base de Dados).
 */
public interface InternalTransactionPort {
    Optional<InternalTransaction> findByRrnAndAuthCode(String rrn, String authCode);
}
