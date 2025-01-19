package com.expensemanager.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryExecutor {
    
    public static List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            // Execute and get results
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        }
        
        return results;
    }
    
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            // Execute update
            return stmt.executeUpdate();
        }
    }
    
    // Example usage methods
    
    public static Map<String, Double> getCategoryTotals(int userId) throws SQLException {
        String sql = "SELECT category, SUM(amount) as total FROM expenses " +
                    "WHERE user_id = ? GROUP BY category";
        
        List<Map<String, Object>> results = executeQuery(sql, userId);
        Map<String, Double> totals = new HashMap<>();
        
        for (Map<String, Object> row : results) {
            String category = (String) row.get("category");
            Double total = ((Number) row.get("total")).doubleValue();
            totals.put(category, total);
        }
        
        return totals;
    }
    
    public static Map<String, Object> getMonthlyExpenseSummary(int userId, String yearMonth) throws SQLException {
        String sql = "SELECT COUNT(*) as transaction_count, " +
                    "SUM(amount) as total_amount, " +
                    "AVG(amount) as average_amount, " +
                    "MIN(amount) as min_amount, " +
                    "MAX(amount) as max_amount " +
                    "FROM expenses " +
                    "WHERE user_id = ? AND strftime('%Y-%m', date) = ?";
        
        List<Map<String, Object>> results = executeQuery(sql, userId, yearMonth);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
    
    public static List<Map<String, Object>> getTopSpendingCategories(int userId, int limit) throws SQLException {
        String sql = "SELECT category, SUM(amount) as total_amount, COUNT(*) as transaction_count " +
                    "FROM expenses WHERE user_id = ? " +
                    "GROUP BY category ORDER BY total_amount DESC LIMIT ?";
        
        return executeQuery(sql, userId, limit);
    }
} 