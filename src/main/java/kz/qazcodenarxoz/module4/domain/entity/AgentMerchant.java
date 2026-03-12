package kz.qazcodenarxoz.module4.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agent_merchants")
@Getter
@Setter
public class AgentMerchant {

    @EmbeddedId
    private AgentMerchantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("agentId")
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("merchantId")
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}