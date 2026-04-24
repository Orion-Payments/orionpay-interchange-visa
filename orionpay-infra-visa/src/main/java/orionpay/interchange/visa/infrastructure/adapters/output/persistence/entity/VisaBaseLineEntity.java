package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class VisaBaseLineEntity extends VisaBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "raw_line_content", length = 170)
    private String rawLineContent;
}
