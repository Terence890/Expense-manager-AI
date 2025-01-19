package com.expensemanager.dao;

import com.expensemanager.models.Expense;
import com.expensemanager.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.*;

public class ExpenseDAO {
    
    public BigDecimal getTotalExpenses(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM expenses WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
                return BigDecimal.ZERO;
            }
        }
    }
    
    public Map<String, BigDecimal> getExpensesByDateRange(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT c.name, COALESCE(SUM(e.amount), 0) as total " +
                    "FROM expenses e " +
                    "JOIN categories c ON e.category_id = c.id " +
                    "WHERE e.user_id = ? AND e.date BETWEEN ? AND ? " +
                    "GROUP BY c.id, c.name ORDER BY total DESC";
        
        Map<String, BigDecimal> expenses = new LinkedHashMap<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("name");
                    BigDecimal total = rs.getBigDecimal("total");
                    expenses.put(category, total);
                }
            }
        }
        
        return expenses;
    }
    
    public Map<String, BigDecimal> getExpensesByCategory(int userId) throws SQLException {
        String sql = "SELECT c.name, COALESCE(SUM(e.amount), 0) as total " +
                    "FROM expenses e " +
                    "JOIN categories c ON e.category_id = c.id " +
                    "WHERE e.user_id = ? " +
                    "GROUP BY c.id, c.name ORDER BY total DESC";
        
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("name");
                    BigDecimal total = rs.getBigDecimal("total");
                    categoryTotals.put(category, total);
                }
            }
        }
        
        return categoryTotals;
    }
    
    public List<Map<String, Object>> getRecentExpenses(int userId, int limit) throws SQLException {
        String sql = "SELECT e.id, e.amount, c.name as category, e.date, e.description " +
                    "FROM expenses e " +
                    "JOIN categories c ON e.category_id = c.id " +
                    "WHERE e.user_id = ? ORDER BY e.date DESC, e.id DESC LIMIT ?";
        
        List<Map<String, Object>> expenses = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, Math.min(limit, 100));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> expense = new HashMap<>();
                    expense.put("id", rs.getInt("id"));
                    expense.put("amount", rs.getBigDecimal("amount"));
                    expense.put("category", rs.getString("category"));
                    Date date = rs.getDate("date");
                    expense.put("date", date != null ? date.toLocalDate() : null);
                    expense.put("description", rs.getString("description"));
                    expenses.add(expense);
                }
            }
        }
        
        return expenses;
    }
    
    public void createExpense(Expense expense) throws SQLException {
        // First get category_id from category_name
        String getCategoryIdSql = "SELECT id FROM categories WHERE name = ?";
        String insertExpenseSql = "INSERT INTO expenses (user_id, amount, date, description, category_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get category ID
            int categoryId;
            try (PreparedStatement stmt = conn.prepareStatement(getCategoryIdSql)) {
                stmt.setString(1, expense.getCategoryName());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Category not found: " + expense.getCategoryName());
                    }
                    categoryId = rs.getInt("id");
                }
            }
            
            // Insert expense with category ID
            try (PreparedStatement stmt = conn.prepareStatement(insertExpenseSql)) {
                stmt.setInt(1, expense.getUserId());
                stmt.setBigDecimal(2, expense.getAmount());
                stmt.setDate(3, java.sql.Date.valueOf(expense.getDate()));
                stmt.setString(4, expense.getDescription());
                stmt.setInt(5, categoryId);
                stmt.executeUpdate();
            }
        }
    }
    
    public void updateExpense(Expense expense) throws SQLException {
        // First get category_id from category_name
        String getCategoryIdSql = "SELECT id FROM categories WHERE name = ?";
        String updateExpenseSql = "UPDATE expenses SET amount = ?, date = ?, description = ?, category_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get category ID
            int categoryId;
            try (PreparedStatement stmt = conn.prepareStatement(getCategoryIdSql)) {
                stmt.setString(1, expense.getCategoryName());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Category not found: " + expense.getCategoryName());
                    }
                    categoryId = rs.getInt("id");
                }
            }
            
            // Update expense
            try (PreparedStatement stmt = conn.prepareStatement(updateExpenseSql)) {
                stmt.setBigDecimal(1, expense.getAmount());
                stmt.setDate(2, java.sql.Date.valueOf(expense.getDate()));
                stmt.setString(3, expense.getDescription());
                stmt.setInt(4, categoryId);
                stmt.setInt(5, expense.getId());
                stmt.executeUpdate();
            }
        }
    }
    
    public void deleteExpense(int expenseId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, expenseId);
            stmt.executeUpdate();
        }
    }
}