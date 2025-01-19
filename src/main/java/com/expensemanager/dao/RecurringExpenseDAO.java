package com.expensemanager.dao;

import com.expensemanager.models.RecurringExpense;
import com.expensemanager.utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecurringExpenseDAO {
    
    public RecurringExpense createRecurringExpense(RecurringExpense expense) throws SQLException {
        String sql = "INSERT INTO recurring_expenses (user_id, category_id, amount, description, " +
                    "frequency, start_date, end_date, is_active) " +
                    "SELECT ?, id, ?, ?, ?, ?, ?, ? FROM categories WHERE name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, expense.getUserId());
            pstmt.setBigDecimal(2, expense.getAmount());
            pstmt.setString(3, expense.getDescription());
            pstmt.setString(4, expense.getFrequency());
            pstmt.setDate(5, Date.valueOf(expense.getStartDate()));
            pstmt.setDate(6, Date.valueOf(expense.getEndDate()));
            pstmt.setBoolean(7, expense.isActive());
            pstmt.setString(8, expense.getCategory());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating recurring expense failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getInt(1));
                    return expense;
                } else {
                    throw new SQLException("Creating recurring expense failed, no ID obtained.");
                }
            }
        }
    }

    public List<RecurringExpense> findByUserId(int userId) throws SQLException {
        String sql = "SELECT r.*, c.name as category_name FROM recurring_expenses r " +
                    "JOIN categories c ON r.category_id = c.id " +
                    "WHERE r.user_id = ? AND r.is_active = true " +
                    "AND r.end_date >= CURRENT_DATE " +
                    "ORDER BY r.start_date";
        
        List<RecurringExpense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RecurringExpense expense = new RecurringExpense(
                        rs.getInt("user_id"),
                        rs.getString("category_name"),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"),
                        rs.getString("frequency"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                    );
                    expense.setId(rs.getInt("id"));
                    expense.setActive(rs.getBoolean("is_active"));
                    expenses.add(expense);
                }
            }
        }
        return expenses;
    }

    public boolean updateRecurringExpense(RecurringExpense expense) throws SQLException {
        String sql = "UPDATE recurring_expenses r " +
                    "SET amount = ?, description = ?, frequency = ?, " +
                    "start_date = ?, end_date = ?, is_active = ?, " +
                    "category_id = (SELECT id FROM categories WHERE name = ?) " +
                    "WHERE r.id = ? AND r.user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, expense.getAmount());
            pstmt.setString(2, expense.getDescription());
            pstmt.setString(3, expense.getFrequency());
            pstmt.setDate(4, Date.valueOf(expense.getStartDate()));
            pstmt.setDate(5, Date.valueOf(expense.getEndDate()));
            pstmt.setBoolean(6, expense.isActive());
            pstmt.setString(7, expense.getCategory());
            pstmt.setInt(8, expense.getId());
            pstmt.setInt(9, expense.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteRecurringExpense(int expenseId, int userId) throws SQLException {
        String sql = "UPDATE recurring_expenses SET is_active = false " +
                    "WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, expenseId);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<RecurringExpense> findDueExpenses() throws SQLException {
        String sql = "SELECT r.*, c.name as category_name FROM recurring_expenses r " +
                    "JOIN categories c ON r.category_id = c.id " +
                    "WHERE r.is_active = true " +
                    "AND r.end_date >= CURRENT_DATE " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM expenses e " +
                    "    WHERE e.recurring_expense_id = r.id " +
                    "    AND DATE(e.date) = CURRENT_DATE" +
                    ")";
        
        List<RecurringExpense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RecurringExpense expense = new RecurringExpense(
                        rs.getInt("user_id"),
                        rs.getString("category_name"),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"),
                        rs.getString("frequency"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                    );
                    expense.setId(rs.getInt("id"));
                    expense.setActive(rs.getBoolean("is_active"));
                    expenses.add(expense);
                }
            }
        }
        return expenses;
    }
} 