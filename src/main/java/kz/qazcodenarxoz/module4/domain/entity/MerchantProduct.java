package kz.qazcodenarxoz.module4.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "merchant_products")
@Getter
@Setter
public class MerchantProduct {

    @EmbeddedId
    private MerchantProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("merchantId")
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}