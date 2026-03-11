package kz.qazcodenarxoz.module4.repository;

import kz.qazcodenarxoz.module4.domain.dto.TransactionReportFilter;
import kz.qazcodenarxoz.module4.domain.dto.TransactionReportItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionReportRepositoryCustom {
    Page<TransactionReportItemDto> findReport(TransactionReportFilter filter, Pageable pageable);
}