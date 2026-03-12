package kz.qazcodenarxoz.module4.controller;

import kz.qazcodenarxoz.module4.specification.TransactionSpecification;
import kz.qazcodenarxoz.module4.domain.dto.TransactionReportItemDto;
import kz.qazcodenarxoz.module4.service.TransactionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/transactions")
@RequiredArgsConstructor
public class TransactionReportController {

    private final TransactionReportService service;

    @GetMapping
    public Page<TransactionReportItemDto> getReport(TransactionSpecification spec, Pageable pageable) {
        return service.getReport(spec, pageable);
    }

}