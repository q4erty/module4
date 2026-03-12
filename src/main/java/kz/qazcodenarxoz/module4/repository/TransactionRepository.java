package kz.qazcodenarxoz.module4.repository;

import kz.qazcodenarxoz.module4.domain.entity.TransactionEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID>, JpaSpecificationExecutor<TransactionEntity> {

    @NonNull
    Page<TransactionEntity> findAll(Specification<TransactionEntity> spec, @NonNull Pageable pageable);

}
