package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Modelo de domínio complementar para os detalhes de liquidação.
 * Usualmente fornecidos em registros TCR1 (Transaction Component Record 1) ou TC10/TC50 anexos.
 * Contêm as informações do lojista (Merchant), Endereço, MCC, etc.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaSettlementDetails {
    private Long id;
    private Long masterSettlementId; // Referência para a transação "Pai" (VisaSettlementMaster)
    private String merchantDbaName; // Doing Business As (Nome fantasia)
    private String merchantLegalName; // Nome Legal/Razão Social
    private String merchantAddressLine1;
    private String merchantAddressLine2;
    private String merchantCity;
    private String mccPrimary; // Merchant Category Code (4 posições numéricas)
    private String mccSecondary;
    private String acquiringBin1; // BIN principal do adquirente (6 a 8 posições)
    private String cardAcceptorId1; // ID do Terminal ou ID de Aceitação no POS
    private String acquiringBin2;
    private String cardAcceptorId2;

    /**
     * Valida se a transação possui pelo menos um MCC primário válido.
     */
    public boolean hasValidMcc() {
        return mccPrimary != null && mccPrimary.trim().length() == 4;
    }
}
