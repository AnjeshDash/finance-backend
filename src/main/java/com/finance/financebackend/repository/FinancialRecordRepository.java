package com.finance.financebackend.repository;

import com.finance.financebackend.entity.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findByIsDeletedFalse();

    List<FinancialRecord> findByUserIdAndIsDeletedFalse(Long userId);

    List<FinancialRecord> findByTypeAndIsDeletedFalse(String type);

    List<FinancialRecord> findByCategoryAndIsDeletedFalse(String category);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = :type AND f.isDeleted = false")
    BigDecimal sumByType(@Param("type") String type);

    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f WHERE f.isDeleted = false GROUP BY f.category")
    List<Object[]> getCategoryWiseTotals();

    List<FinancialRecord> findByDateBetweenAndIsDeletedFalse(LocalDate startDate, LocalDate endDate);

    @Query("SELECT f FROM FinancialRecord f WHERE f.isDeleted = false AND (LOWER(f.notes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<FinancialRecord> searchByKeyword(@Param("keyword") String keyword);

    Page<FinancialRecord> findByIsDeletedFalse(Pageable pageable);
}