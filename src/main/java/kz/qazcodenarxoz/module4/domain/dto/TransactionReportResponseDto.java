package kz.qazcodenarxoz.module4.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReportResponseDto {
    private int page;
    private int size;
    private long total;
    private List<TransactionReportItemDto> items;
}