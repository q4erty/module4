package kz.qazcodenarxoz.module4.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import kz.qazcodenarxoz.module4.domain.dto.TransactionReportFilter;
import kz.qazcodenarxoz.module4.domain.dto.TransactionReportItemDto;
import kz.qazcodenarxoz.module4.domain.entity.*;
import kz.qazcodenarxoz.module4.domain.enumeration.CommissionRuleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionReportRepositoryCustomImpl implements TransactionReportRepositoryCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<TransactionReportItemDto> findReport(TransactionReportFilter filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<TransactionEntity> cq = cb.createQuery(TransactionEntity.class);
        Root<TransactionEntity> tx = cq.from(TransactionEntity.class);

        tx.fetch("merchant", JoinType.INNER);
        tx.fetch("agent", JoinType.INNER);
        tx.fetch("product", JoinType.INNER);
        tx.fetch("buyerUser", JoinType.INNER);

        Join<TransactionEntity, Merchant> merchant = tx.join("merchant", JoinType.INNER);
        Join<TransactionEntity, Agent> agent = tx.join("agent", JoinType.INNER);
        Join<TransactionEntity, Product> product = tx.join("product", JoinType.INNER);
        Join<TransactionEntity, AppUser> buyer = tx.join("buyerUser", JoinType.INNER);

        List<Predicate> predicates = buildPredicates(filter, cb, cq, tx, merchant, agent, product, buyer);

        cq.select(tx).distinct(true);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(buildOrders(cb, tx, merchant, agent, product, pageable));

        TypedQuery<TransactionEntity> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<TransactionReportItemDto> items = query.getResultList().stream()
                .map(this::toDto)
                .filter(dto -> matchesCommissionFilter(dto, filter))
                .toList();

        long total = countReport(filter);

        return new PageImpl<>(items, pageable, total);
    }

    private List<Predicate> buildPredicates(
            TransactionReportFilter filter,
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<TransactionEntity> tx,
            Join<TransactionEntity, Merchant> merchant,
            Join<TransactionEntity, Agent> agent,
            Join<TransactionEntity, Product> product,
            Join<TransactionEntity, AppUser> buyer
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(tx.get("createdAt"), filter.getFrom()));
        }

        if (filter.getTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(tx.get("createdAt"), filter.getTo()));
        }

        if (filter.getMerchantCode() != null && !filter.getMerchantCode().isBlank()) {
            predicates.add(cb.equal(merchant.get("code"), filter.getMerchantCode()));
        }

        if (filter.getAgentCode() != null && !filter.getAgentCode().isBlank()) {
            predicates.add(cb.equal(agent.get("code"), filter.getAgentCode()));
        }

        if (filter.getProductCode() != null && !filter.getProductCode().isBlank()) {
            predicates.add(cb.equal(product.get("code"), filter.getProductCode()));
        }

        if (filter.getBuyerEmail() != null && !filter.getBuyerEmail().isBlank()) {
            predicates.add(cb.equal(buyer.get("email"), filter.getBuyerEmail()));
        }

        if (filter.getBuyerExternalId() != null && !filter.getBuyerExternalId().isBlank()) {
            predicates.add(cb.equal(buyer.get("externalId"), filter.getBuyerExternalId()));
        }

        if (filter.getSuccess() != null) {
            predicates.add(cb.equal(tx.get("success"), filter.getSuccess()));
        }

        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
            predicates.add(tx.get("status").in(filter.getStatuses()));
        }

        if (filter.getResultCodes() != null && !filter.getResultCodes().isEmpty()) {
            predicates.add(tx.get("resultCode").in(filter.getResultCodes()));
        }

        if (filter.getMinAmount() != null) {
            predicates.add(cb.greaterThanOrEqualTo(tx.get("amount"), filter.getMinAmount()));
        }

        if (filter.getMaxAmount() != null) {
            predicates.add(cb.lessThanOrEqualTo(tx.get("amount"), filter.getMaxAmount()));
        }

        if (filter.getCurrency() != null && !filter.getCurrency().isBlank()) {
            predicates.add(cb.equal(tx.get("currency"), filter.getCurrency()));
        }

        if (filter.getHasFeeRule() != null) {
            Subquery<Long> feeSubquery = buildFeeExistsSubquery(cb, cq, tx);

            if (Boolean.TRUE.equals(filter.getHasFeeRule())) {
                predicates.add(cb.exists(feeSubquery));
            } else {
                predicates.add(cb.not(cb.exists(feeSubquery)));
            }
        }

        return predicates;
    }

    private Subquery<Long> buildFeeExistsSubquery(
            CriteriaBuilder cb,
            CriteriaQuery<?> parentQuery,
            Root<TransactionEntity> tx
    ) {
        Subquery<Long> sq = parentQuery.subquery(Long.class);
        Root<Fee> fee = sq.from(Fee.class);

        sq.select(cb.literal(1L));
        sq.where(
                cb.equal(fee.get("merchant").get("id"), tx.get("merchant").get("id")),
                cb.equal(fee.get("agent").get("id"), tx.get("agent").get("id")),
                cb.equal(fee.get("product").get("id"), tx.get("product").get("id")),
                cb.equal(fee.get("currency"), tx.get("currency")),
                cb.isTrue(fee.get("isActive")),
                cb.lessThanOrEqualTo(fee.get("validFrom"), tx.get("createdAt")),
                cb.or(
                        cb.isNull(fee.get("validTo")),
                        cb.greaterThanOrEqualTo(fee.get("validTo"), tx.get("createdAt"))
                )
        );

        return sq;
    }

    private List<Order> buildOrders(
            CriteriaBuilder cb,
            Root<TransactionEntity> tx,
            Join<TransactionEntity, Merchant> merchant,
            Join<TransactionEntity, Agent> agent,
            Join<TransactionEntity, Product> product,
            Pageable pageable
    ) {
        List<Order> orders = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            Path<?> path = switch (sortOrder.getProperty()) {
                case "createdAt" -> tx.get("createdAt");
                case "amount" -> tx.get("amount");
                case "status" -> tx.get("status");
                case "merchantCode" -> merchant.get("code");
                case "agentCode" -> agent.get("code");
                case "productCode" -> product.get("code");
                default -> tx.get("createdAt");
            };

            orders.add(sortOrder.isAscending() ? cb.asc(path) : cb.desc(path));
        }

        if (orders.isEmpty()) {
            orders.add(cb.desc(tx.get("createdAt")));
        }

        return orders;
    }

    private TransactionReportItemDto toDto(TransactionEntity tx) {
        Fee fee = findApplicableFee(tx);
        BigDecimal commissionAmount = calculateCommission(tx.getAmount(), fee);
        String commissionRuleStatus = resolveCommissionRuleStatus(fee, tx.getCurrency());

        return TransactionReportItemDto.builder()
                .txId(tx.getTxId())
                .createdAt(tx.getCreatedAt())
                .finishedAt(tx.getFinishedAt())
                .status(tx.getStatus().name())
                .success(tx.getSuccess())
                .resultCode(tx.getResultCode() != null ? tx.getResultCode().name() : null)
                .amount(tx.getAmount())
                .currency(tx.getCurrency())
                .productCode(tx.getProduct().getCode())
                .productName(tx.getProduct().getName())
                .productCategory(tx.getProduct().getCategory())
                .merchantCode(tx.getMerchant().getCode())
                .merchantName(tx.getMerchant().getName())
                .merchantMcc(tx.getMerchant().getMcc())
                .merchantCountry(tx.getMerchant().getCountry())
                .buyerExternalId(tx.getBuyerUser().getExternalId())
                .buyerEmail(tx.getBuyerUser().getEmail())
                .buyerFullName(tx.getBuyerUser().getFullName())
                .agentCode(tx.getAgent().getCode())
                .agentName(tx.getAgent().getName())
                .agentType(tx.getAgent().getType().name())
                .feeCalcType(fee != null ? fee.getCalcType().name() : null)
                .feeRuleCurrency(fee != null ? fee.getCurrency() : null)
                .feeRate(fee != null ? fee.getRate() : null)
                .feeFixedAmount(fee != null ? fee.getFixedAmount() : null)
                .feeMinAmount(fee != null ? fee.getMinAmount() : null)
                .feeMaxAmount(fee != null ? fee.getMaxAmount() : null)
                .commissionAmount(commissionAmount)
                .commissionRuleStatus(commissionRuleStatus)
                .build();
    }

    private Fee findApplicableFee(TransactionEntity tx) {
        String jpql = """
                select f from Fee f
                where f.merchant.id = :merchantId
                    and f.agent.id = :agentId
                    and f.product.id = :productId
                    and f.currency = :currency
                    and f.isActive = true
                    and f.validFrom <= :createdAt
                    and (f.validTo is null or f.validTo >= :createdAt)
                order by f.validFrom desc
                """;

        List<Fee> fees = entityManager.createQuery(jpql, Fee.class)
                .setParameter("merchantId", tx.getMerchant().getId())
                .setParameter("agentId", tx.getAgent().getId())
                .setParameter("productId", tx.getProduct().getId())
                .setParameter("currency", tx.getCurrency())
                .setParameter("createdAt", tx.getCreatedAt())
                .setMaxResults(1)
                .getResultList();

        return fees.isEmpty() ? null : fees.getFirst();
    }

    private BigDecimal calculateCommission(BigDecimal amount, Fee fee) {
        if (fee == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = switch (fee.getCalcType()) {
            case PERCENT -> amount.multiply(fee.getRate());
            case FIXED -> fee.getFixedAmount();
            case PERCENT_WITH_CAP -> amount.multiply(fee.getRate());
        };

        if (fee.getMinAmount() != null && result.compareTo(fee.getMinAmount()) < 0) {
            result = fee.getMinAmount();
        }

        if (fee.getMaxAmount() != null && result.compareTo(fee.getMaxAmount()) > 0) {
            result = fee.getMaxAmount();
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    private String resolveCommissionRuleStatus(Fee fee, String txCurrency) {
        if (fee == null) {
            return CommissionRuleStatus.NO_FEE_RULE.name();
        }

        if (!fee.getCurrency().equals(txCurrency)) {
            return CommissionRuleStatus.CURRENCY_MISMATCH.name();
        }

        return CommissionRuleStatus.APPLIED.name();
    }

    private boolean matchesCommissionFilter(TransactionReportItemDto dto, TransactionReportFilter filter) {
        if (filter.getMinCommission() != null &&
                dto.getCommissionAmount().compareTo(filter.getMinCommission()) < 0) {
            return false;
        }

        if (filter.getMaxCommission() != null &&
                dto.getCommissionAmount().compareTo(filter.getMaxCommission()) > 0) {
            return false;
        }

        return true;
    }

    private long countReport(TransactionReportFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TransactionEntity> tx = cq.from(TransactionEntity.class);

        Join<TransactionEntity, Merchant> merchant = tx.join("merchant", JoinType.INNER);
        Join<TransactionEntity, Agent> agent = tx.join("agent", JoinType.INNER);
        Join<TransactionEntity, Product> product = tx.join("product", JoinType.INNER);
        Join<TransactionEntity, AppUser> buyer = tx.join("buyerUser", JoinType.INNER);

        List<Predicate> predicates = buildPredicates(filter, cb, cq, tx, merchant, agent, product, buyer);

        cq.select(cb.countDistinct(tx));
        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getSingleResult();
    }
}