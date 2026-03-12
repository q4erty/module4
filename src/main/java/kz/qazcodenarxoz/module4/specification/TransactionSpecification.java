package kz.qazcodenarxoz.module4.specification;

import kz.qazcodenarxoz.module4.domain.entity.TransactionEntity;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "txId", spec = Equal.class),
        @Spec(path = "merchant.code", params = "merchantCode", spec = Equal.class),
        @Spec(path = "agent.code", params = "agentCode", spec = Equal.class),
        @Spec(path = "product.code", params = "productCode", spec = Equal.class),
        @Spec(path = "buyerUser.email", params = "buyerEmail", spec = Equal.class),
        @Spec(path = "amount", params = "minAmount", spec = GreaterThanOrEqual.class),
        @Spec(path = "amount", params = "maxAmount", spec = LessThanOrEqual.class),
        @Spec(path = "currency", spec = Equal.class),
        @Spec(path = "success", spec = Equal.class)
})
public interface TransactionSpecification extends Specification<TransactionEntity> {
}