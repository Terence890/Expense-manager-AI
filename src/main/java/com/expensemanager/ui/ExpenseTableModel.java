package com.expensemanager.ui;

import com.expensemanager.models.Expense;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpenseTableModel extends AbstractTableModel {
    private List<Expense> expenses = new ArrayList<>();
    private final String[] columnNames = {"Date", "Category", "Description", "Amount"};
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public int getRowCount() {
        return expenses.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Expense expense = expenses.get(rowIndex);
        switch (columnIndex) {
            case 0: return expense.getDate().format(dateFormatter);
            case 1: return expense.getCategoryName();
            case 2: return expense.getDescription();
            case 3: return String.format("$%.2f", expense.getAmount());
            default: return null;
        }
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        fireTableDataChanged();
    }

    public Expense getExpenseAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < expenses.size()) {
            return expenses.get(rowIndex);
        }
        return null;
    }

    public void updateExpense(Expense expense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == expense.getId()) {
                expenses.set(i, expense);
                fireTableRowsUpdated(i, i);
                break;
            }
        }
    }

    public void deleteExpense(int expenseId) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == expenseId) {
                expenses.remove(i);
                fireTableRowsDeleted(i, i);
                break;
            }
        }
    }

    public void refreshData() {
        fireTableDataChanged();
    }
} 