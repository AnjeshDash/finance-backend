package com.finance.financebackend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDate date;
    private String notes;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}