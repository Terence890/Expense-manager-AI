package com.expensemanager.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Budget {
    private int id;
    private int userId;
    private String category;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;

    public Budget() {}

    public Budget(int userId, String category, BigDecimal amount, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
} 