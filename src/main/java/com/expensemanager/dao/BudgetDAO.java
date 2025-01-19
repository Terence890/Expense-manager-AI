package com.expensemanager.dao;

import com.expensemanager.models.Budget;
import com.expensemanager.utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class BudgetDAO {
    
    public Budget createBudget(Budget budget) throws SQLException {
        String sql = "INSERT INTO budgets (user_id, category_id, amount, start_date, end_date) " +
                    "SELECT ?, id, ?, ?, ? FROM categories WHERE name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, budget.getUserId());
            pstmt.setBigDecimal(2, budget.getAmount());
            pstmt.setDate(3, Date.valueOf(budget.getStartDate()));
            pstmt.setDate(4, Date.valueOf(budget.getEndDate()));
            pstmt.setString(5, budget.getCategory());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating budget failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    budget.setId(generatedKeys.getInt(1));
                    return budget;
                } else {
                    throw new SQLException("Creating budget failed, no ID obtained.");
                }
            }
        }
    }

    public List<Budget> findByUserId(int userId) throws SQLException {
        String sql = "SELECT b.*, c.name as category_name FROM budgets b " +
                    "JOIN categories c ON b.category_id = c.id " +
                    "WHERE b.user_id = ? AND b.end_date >= CURRENT_DATE " +
                    "ORDER BY b.start_date";
        
        List<Budget> budgets = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Budget budget = new Budget(
                        rs.getInt("user_id"),
                        rs.getString("category_name"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                    );
                    budget.setId(rs.getInt("id"));
                    budgets.add(budget);
                }
            }
        }
        return budgets;
    }

    public BigDecimal getCurrentBudget(int userId, String category) throws SQLException {
        String sql = "SELECT b.amount FROM budgets b " +
                    "JOIN categories c ON b.category_id = c.id " +
                    "WHERE b.user_id = ? AND c.name = ? " +
                    "AND CURRENT_DATE BETWEEN b.start_date AND b.end_date";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("amount");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public boolean updateBudget(Budget budget) throws SQLException {
        String sql = "UPDATE budgets b " +
                    "SET amount = ?, start_date = ?, end_date = ?, category_id = " +
                    "(SELECT id FROM categories WHERE name = ?) " +
                    "WHERE b.id = ? AND b.user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, budget.getAmount());
            pstmt.setDate(2, Date.valueOf(budget.getStartDate()));
            pstmt.setDate(3, Date.valueOf(budget.getEndDate()));
            pstmt.setString(4, budget.getCategory());
            pstmt.setInt(5, budget.getId());
            pstmt.setInt(6, budget.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteBudget(int budgetId, int userId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, budgetId);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
} 