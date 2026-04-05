package com.finance.financebackend.service;

import com.finance.financebackend.dto.request.CreateRecordRequestDTO;
import com.finance.financebackend.dto.response.FinancialRecordResponseDTO;
import com.finance.financebackend.entity.FinancialRecord;
import com.finance.financebackend.entity.User;
import com.finance.financebackend.repository.FinancialRecordRepository;
import com.finance.financebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    private User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private FinancialRecordResponseDTO toDTO(FinancialRecord record) {
        return FinancialRecordResponseDTO.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .userName(record.getUser().getName())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    public FinancialRecordResponseDTO createRecord(CreateRecordRequestDTO request) {
        User currentUser = getCurrentUser();

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType().toLowerCase())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .user(currentUser)
                .isDeleted(false)
                .build();

        FinancialRecord saved = recordRepository.save(record);
        log.info("Record created by {}: {}", currentUser.getEmail(), saved.getId());
        return toDTO(saved);
    }

    public Page<FinancialRecordResponseDTO> getAllRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return recordRepository
                .findByIsDeletedFalse(pageable)
                .map(this::toDTO);
    }

    public FinancialRecordResponseDTO getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found: " + id));

        if (record.getIsDeleted()) {
            throw new RuntimeException("Record not found: " + id);
        }
        return toDTO(record);
    }

    public FinancialRecordResponseDTO updateRecord(Long id,
                                                   CreateRecordRequestDTO request) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found: " + id));

        if (record.getIsDeleted()) {
            throw new RuntimeException("Record not found: " + id);
        }

        record.setAmount(request.getAmount());
        record.setType(request.getType().toLowerCase());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());

        FinancialRecord updated = recordRepository.save(record);
        log.info("Record updated: {}", id);
        return toDTO(updated);
    }

    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found: " + id));

        record.setIsDeleted(true);
        recordRepository.save(record);
        log.info("Record soft deleted: {}", id);
    }

    public List<FinancialRecordResponseDTO> filterRecords(
            String type, String category,
            LocalDate startDate, LocalDate endDate) {

        List<FinancialRecord> records;

        if (startDate != null && endDate != null) {
            records = recordRepository
                    .findByDateBetweenAndIsDeletedFalse(startDate, endDate);
        } else if (type != null && !type.isEmpty()) {
            records = recordRepository.findByTypeAndIsDeletedFalse(type);
        } else if (category != null && !category.isEmpty()) {
            records = recordRepository.findByCategoryAndIsDeletedFalse(category);
        } else {
            records = recordRepository.findByIsDeletedFalse();
        }

        return records.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<FinancialRecordResponseDTO> searchRecords(String keyword) {
        return recordRepository.searchByKeyword(keyword)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
}