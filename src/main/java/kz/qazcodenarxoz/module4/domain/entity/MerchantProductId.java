package kz.qazcodenarxoz.module4.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MerchantProductId implements Serializable {

    @Column(name = "merchant_id", length = 36, nullable = false)
    private UUID merchantId;

    @Column(name = "product_id", length = 36, nullable = false)
    private UUID productId;
}