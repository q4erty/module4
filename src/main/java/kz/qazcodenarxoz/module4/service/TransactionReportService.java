package kz.qazcodenarxoz.module4.service;

import kz.qazcodenarxoz.module4.domain.dto.TransactionReportItemDto;
import kz.qazcodenarxoz.module4.domain.entity.TransactionEntity;
import kz.qazcodenarxoz.module4.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionReportService {

    private final TransactionRepository repository;

    @Transactional(readOnly = true)
    public Page<TransactionReportItemDto> getReport(Specification<TransactionEntity> spec, Pageable pageable) {
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    private TransactionReportItemDto toDto(TransactionEntity tx) {

        return TransactionReportItemDto.builder()
                .txId(tx.getTxId())
                .createdAt(tx.getCreatedAt())
                .status(tx.getStatus().name())
                .amount(tx.getAmount())
                .currency(tx.getCurrency())
                .merchantCode(tx.getMerchant().getCode())
                .agentCode(tx.getAgent().getCode())
                .productCode(tx.getProduct().getCode())
                .buyerEmail(tx.getBuyerUser().getEmail())
                .build();
    }
}