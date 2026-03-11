package kz.qazcodenarxoz.module4.domain.dto;

import kz.qazcodenarxoz.module4.domain.enumeration.ResultCode;
import kz.qazcodenarxoz.module4.domain.enumeration.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class TransactionReportFilter {

    private Instant from;
    private Instant to;
    private String merchantCode;
    private String agentCode;
    private String productCode;
    private String buyerEmail;
    private String buyerExternalId;
    private Boolean success;
    private List<Status> statuses;
    private List<ResultCode> resultCodes;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String currency;
    private BigDecimal minCommission;
    private BigDecimal maxCommission;
    private Boolean hasFeeRule;
}