package kz.qazcodenarxoz.module4.controller;

import kz.qazcodenarxoz.module4.domain.dto.TransactionReportFilter;
import kz.qazcodenarxoz.module4.domain.dto.TransactionReportResponseDto;
import kz.qazcodenarxoz.module4.domain.enumeration.ResultCode;
import kz.qazcodenarxoz.module4.domain.enumeration.Status;
import kz.qazcodenarxoz.module4.service.TransactionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/reports/transactions")
@RequiredArgsConstructor
public class TransactionReportController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 200;

    private final TransactionReportService transactionReportService;

    @GetMapping
    public TransactionReportResponseDto getTransactions(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String merchantCode,
            @RequestParam(required = false) String agentCode,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String buyerEmail,
            @RequestParam(required = false) String buyerExternalId,
            @RequestParam(required = false) Boolean success,
            @RequestParam(name = "status", required = false) List<Status> statuses,
            @RequestParam(name = "resultCode", required = false) List<ResultCode> resultCodes,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) BigDecimal minCommission,
            @RequestParam(required = false) BigDecimal maxCommission,
            @RequestParam(required = false) Boolean hasFeeRule,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        TransactionReportFilter filter = buildFilter(
                from,
                to,
                merchantCode,
                agentCode,
                productCode,
                buyerEmail,
                buyerExternalId,
                success,
                statuses,
                resultCodes,
                minAmount,
                maxAmount,
                currency,
                minCommission,
                maxCommission,
                hasFeeRule
        );

        Pageable pageable = buildPageable(page, size, sort);

        return transactionReportService.getReport(filter, pageable);
    }

    private TransactionReportFilter buildFilter(
            Instant from,
            Instant to,
            String merchantCode,
            String agentCode,
            String productCode,
            String buyerEmail,
            String buyerExternalId,
            Boolean success,
            List<Status> statuses,
            List<ResultCode> resultCodes,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String currency,
            BigDecimal minCommission,
            BigDecimal maxCommission,
            Boolean hasFeeRule
    ) {
        TransactionReportFilter filter = new TransactionReportFilter();
        filter.setFrom(from);
        filter.setTo(to);
        filter.setMerchantCode(trimToNull(merchantCode));
        filter.setAgentCode(trimToNull(agentCode));
        filter.setProductCode(trimToNull(productCode));
        filter.setBuyerEmail(trimToNull(buyerEmail));
        filter.setBuyerExternalId(trimToNull(buyerExternalId));
        filter.setSuccess(success);
        filter.setStatuses(statuses);
        filter.setResultCodes(resultCodes);
        filter.setMinAmount(minAmount);
        filter.setMaxAmount(maxAmount);
        filter.setCurrency(trimToNull(currency));
        filter.setMinCommission(minCommission);
        filter.setMaxCommission(maxCommission);
        filter.setHasFeeRule(hasFeeRule);
        return filter;
    }

    private Pageable buildPageable(int page, int size, String sort) {
        int normalizedPage = Math.max(page, DEFAULT_PAGE);
        int normalizedSize = normalizeSize(size);
        Sort springSort = parseSort(sort);

        return PageRequest.of(normalizedPage, normalizedSize, springSort);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sort.split(",");
        String property = parts[0].trim();

        if (property.isBlank()) {
            property = "createdAt";
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.ASC;
        }

        return Sort.by(direction, mapSortProperty(property));
    }

    private String mapSortProperty(String property) {
        return switch (property) {
            case "createdAt", "amount", "status", "merchantCode", "agentCode", "productCode" -> property;
            default -> "createdAt";
        };
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}