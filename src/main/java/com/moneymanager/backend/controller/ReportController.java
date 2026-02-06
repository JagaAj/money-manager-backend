package com.moneymanager.backend.controller;

import com.moneymanager.backend.model.Transaction;
import com.moneymanager.backend.model.TransactionType;
import com.moneymanager.backend.service.TransactionService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final TransactionService transactionService;

    public ReportController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        List<Transaction> all = transactionService.getAllTransactions();

        double totalIncome = all.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = all.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        Map<String, Double> expenseCategories = all.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCategory() != null)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));

        Map<String, Double> incomeCategories = all.stream()
                .filter(t -> t.getType() == TransactionType.INCOME && t.getCategory() != null)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));

        Map<String, Object> response = new HashMap<>();
        response.put("totalIncome", totalIncome);
        response.put("totalExpense", totalExpense);
        response.put("balance", totalIncome - totalExpense);
        response.put("expenseCategories", expenseCategories);
        response.put("incomeCategories", incomeCategories);

        return response;
    }

    @GetMapping("/chart/weekly")
    public List<ChartData> getWeeklyData() {
        LocalDateTime start = LocalDateTime.now().minusDays(7).truncatedTo(ChronoUnit.DAYS);
        return getChartData(start);
    }

    @GetMapping("/chart/monthly")
    public List<ChartData> getMonthlyData() {
        LocalDateTime start = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.DAYS);
        return getChartData(start);
    }

    @GetMapping("/chart/yearly")
    public List<ChartData> getYearlyData() {
        LocalDateTime start = LocalDateTime.now().minusDays(365).truncatedTo(ChronoUnit.DAYS);
        return getChartData(start);
    }

    private List<ChartData> getChartData(LocalDateTime start) {
        List<Transaction> transactions = transactionService.filterTransactions(null, null, start, LocalDateTime.now());

        return transactions.stream()
                .filter(t -> t.getTimestamp() != null)
                .collect(Collectors.groupingBy(t -> t.getTimestamp().toLocalDate(), Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {
                    double income = entry.getValue().stream().filter(t -> t.getType() == TransactionType.INCOME)
                            .mapToDouble(Transaction::getAmount).sum();
                    double expense = entry.getValue().stream().filter(t -> t.getType() == TransactionType.EXPENSE)
                            .mapToDouble(Transaction::getAmount).sum();
                    return new ChartData(entry.getKey().toString(), income, expense);
                })
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    static class ChartData {
        private String date;
        private double income;
        private double expense;

        public ChartData(String date, double income, double expense) {
            this.date = date;
            this.income = income;
            this.expense = expense;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getIncome() {
            return income;
        }

        public void setIncome(double income) {
            this.income = income;
        }

        public double getExpense() {
            return expense;
        }

        public void setExpense(double expense) {
            this.expense = expense;
        }
    }
}
