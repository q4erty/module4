package kz.qazcodenarxoz.module4.service;

import kz.qazcodenarxoz.module4.domain.dto.TransactionReportItemDto;
import kz.qazcodenarxoz.module4.domain.entity.Agent;
import kz.qazcodenarxoz.module4.domain.entity.AppUser;
import kz.qazcodenarxoz.module4.domain.entity.Merchant;
import kz.qazcodenarxoz.module4.domain.entity.Product;
import kz.qazcodenarxoz.module4.domain.entity.TransactionEntity;
import kz.qazcodenarxoz.module4.domain.enumeration.Status;
import kz.qazcodenarxoz.module4.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionReportServiceTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionReportService service;

    @Mock
    private Specification<TransactionEntity> spec;

    @Test
    void getReport_shouldReturnMappedDtoPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Merchant merchant = new Merchant();
        merchant.setCode("M001");

        Agent agent = new Agent();
        agent.setCode("A01");

        Product product = new Product();
        product.setCode("P001");

        AppUser user = new AppUser();
        user.setEmail("user1@mail.com");

        Instant createdAt = LocalDateTime.of(2026, 3, 11, 20, 0)
                .toInstant(ZoneOffset.UTC);

        TransactionEntity tx = new TransactionEntity();
        tx.setTxId("TX-123");
        tx.setCreatedAt(createdAt);
        tx.setStatus(Status.SUCCESS);
        tx.setAmount(new BigDecimal("1500.50"));
        tx.setCurrency("KZT");
        tx.setMerchant(merchant);
        tx.setAgent(agent);
        tx.setProduct(product);
        tx.setBuyerUser(user);

        Page<TransactionEntity> entityPage = new PageImpl<>(List.of(tx), pageable, 1);

        when(repository.findAll(spec, pageable)).thenReturn(entityPage);

        Page<TransactionReportItemDto> result = service.getReport(spec, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        TransactionReportItemDto dto = result.getContent().getFirst();
        assertThat(dto.getTxId()).isEqualTo("TX-123");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getStatus()).isEqualTo("SUCCESS");
        assertThat(dto.getAmount()).isEqualByComparingTo("1500.50");
        assertThat(dto.getCurrency()).isEqualTo("KZT");
        assertThat(dto.getMerchantCode()).isEqualTo("M001");
        assertThat(dto.getAgentCode()).isEqualTo("A01");
        assertThat(dto.getProductCode()).isEqualTo("P001");
        assertThat(dto.getBuyerEmail()).isEqualTo("user1@mail.com");

        verify(repository, times(1)).findAll(spec, pageable);
        verifyNoMoreInteractions(repository);
    }
}