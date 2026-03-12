package kz.qazcodenarxoz.module4.service;

import kz.qazcodenarxoz.module4.domain.dto.TransactionReportFilter;
import kz.qazcodenarxoz.module4.domain.dto.TransactionReportItemDto;
import kz.qazcodenarxoz.module4.domain.dto.TransactionReportResponseDto;
import kz.qazcodenarxoz.module4.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionReportService {

    private final TransactionRepository transactionRepository;

    public TransactionReportResponseDto getReport(TransactionReportFilter filter, Pageable pageable) {
        Page<TransactionReportItemDto> page = transactionRepository.findReport(filter, pageable);

        return TransactionReportResponseDto.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .total(page.getTotalElements())
                .items(page.getContent())
                .build();
    }
}