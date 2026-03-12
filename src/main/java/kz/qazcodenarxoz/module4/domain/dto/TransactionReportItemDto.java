package kz.qazcodenarxoz.module4.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReportItemDto {
    private String txId;
    private Instant createdAt;
    private Instant finishedAt;
    private String status;
    private Boolean success;
    private String resultCode;
    private BigDecimal amount;
    private String currency;
    private String productCode;
    private String productName;
    private String productCategory;
    private String merchantCode;
    private String merchantName;
    private String merchantMcc;
    private String merchantCountry;
    private String buyerExternalId;
    private String buyerEmail;
    private String buyerFullName;
    private String agentCode;
    private String agentName;
    private String agentType;
    private String feeCalcType;
    private String feeRuleCurrency;
    private BigDecimal feeRate;
    private BigDecimal feeFixedAmount;
    private BigDecimal feeMinAmount;
    private BigDecimal feeMaxAmount;
    private BigDecimal commissionAmount;
    private String commissionRuleStatus;
}