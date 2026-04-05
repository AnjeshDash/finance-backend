package com.finance.financebackend.service;

import com.finance.financebackend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public Map<String, Object> getSummary() {
        BigDecimal totalIncome = recordRepository.sumByType("income");
        BigDecimal totalExpense = recordRepository.sumByType("expense");
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netBalance", netBalance);
        summary.put("status", netBalance.compareTo(BigDecimal.ZERO) >= 0
                ? "SURPLUS" : "DEFICIT");

        log.info("Dashboard summary fetched");
        return summary;
    }

    public List<Map<String, Object>> getCategoryWiseTotals() {
        List<Object[]> results = recordRepository.getCategoryWiseTotals();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("category", row[0]);
            item.put("total", row[1]);
            response.add(item);
        }
        return response;
    }

    public List<Map<String, Object>> getRecentTransactions() {
        return recordRepository.getRecentTransactions()
                .stream()
                .map(record -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", record.getId());
                    item.put("amount", record.getAmount());
                    item.put("type", record.getType());
                    item.put("category", record.getCategory());
                    item.put("date", record.getDate());
                    item.put("notes", record.getNotes());
                    item.put("userName", record.getUser().getName());
                    return item;
                })
                .toList();
    }

    public List<Map<String, Object>> getMonthlyTrends() {
        List<Object[]> results = recordRepository.getMonthlyTrends();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("year", row[0]);
            item.put("month", row[1]);
            item.put("type", row[2]);
            item.put("total", row[3]);
            response.add(item);
        }
        return response;
    }
}