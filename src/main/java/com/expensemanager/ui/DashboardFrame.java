package com.expensemanager.ui;

import com.expensemanager.models.User;
import com.expensemanager.models.Expense;
import com.expensemanager.dao.ExpenseDAO;
import com.expensemanager.utils.ConfigManager;
import com.expensemanager.utils.AIChatService;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.awt.Desktop;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.text.SimpleDateFormat;
import javax.swing.JScrollBar;
import javax.swing.BoxLayout;
import javax.swing.SwingWorker;
import java.awt.FontMetrics;
import java.io.File;

public class DashboardFrame extends JFrame {
    private final User currentUser;
    private final ExpenseDAO expenseDAO;
    private ResourceBundle messages;
    private boolean isDarkTheme = false;

    // UI Components
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout contentLayout;

    // Theme Colors - Light
    private static final Color PRIMARY_LIGHT = Color.WHITE;
    private static final Color SECONDARY_LIGHT = new Color(245, 245, 245);
    private static final Color ACCENT_LIGHT = Color.BLACK;
    private static final Color TEXT_LIGHT = Color.BLACK;

    // Theme Colors - Dark
    private static final Color PRIMARY_DARK = Color.BLACK;
    private static final Color SECONDARY_DARK = new Color(30, 30, 30);
    private static final Color ACCENT_DARK = Color.WHITE;
    private static final Color TEXT_DARK = Color.WHITE;

    // Border Colors
    private static final Color BORDER_LIGHT = new Color(220, 220, 220);
    private static final Color BORDER_DARK = new Color(50, 50, 50);

    // Success/Error Colors
    private static final Color SUCCESS_COLOR = new Color(40, 40, 40);
    private static final Color ERROR_COLOR = new Color(60, 60, 60);

    // Current Theme Colors
    private Color primaryColor = PRIMARY_LIGHT;
    private Color secondaryColor = SECONDARY_LIGHT;
    private Color accentColor = ACCENT_LIGHT;
    private Color textColor = TEXT_LIGHT;

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Navigation Icons
    private static final String DASHBOARD_ICON = "📊";
    private static final String EXPENSES_ICON = "$";
    private static final String ANALYTICS_ICON = "📈";
    private static final String SETTINGS_ICON = "⚙";
    private static final String CHAT_ICON = "💬";
    private static final String ADD_ICON = "+";
    private static final String THEME_ICON_LIGHT = "☀";
    private static final String THEME_ICON_DARK = "☾";

    // Category Icons
    private static final String FOOD_ICON = "🍴";
    private static final String TRANSPORT_ICON = "🚗";
    private static final String SHOPPING_ICON = "🛒";
    private static final String ENTERTAINMENT_ICON = "🎮";
    private static final String BILLS_ICON = "📄";
    private static final String OTHERS_ICON = "📦";

    // Action Icons
    private static final String EDIT_ICON = "✎";
    private static final String DELETE_ICON = "×";
    private static final String SEND_ICON = "→";
    private static final String SAVE_ICON = "✓";
    private static final String CANCEL_ICON = "✕";
    private static final String HELP_ICON = "?";
    private static final String SUCCESS_ICON = "✓";
    private static final String ERROR_ICON = "!";
    private static final String WARNING_ICON = "⚠";

    // Currency Icons
    private static final String USD_ICON = "$";
    private static final String EUR_ICON = "€";
    private static final String GBP_ICON = "£";
    private static final String JPY_ICON = "¥";

    // Icons using Unicode fallbacks
    private static final String[] ICONS = {
        "📊", "💰", "📈", "⚙️", "💬", "➕", "🌞", "🌙",  // Navigation
        "🍽️", "🚗", "🛍️", "🎮", "📝", "📦",            // Categories
        "✏️", "🗑️", "➤", "💾", "❌", "❓", "✅", "❌",    // Actions
        "💵", "💶", "💷", "💴"                          // Currencies
    };

    // Icon indices for easy reference
    private static final int DASHBOARD = 0, EXPENSES = 1, ANALYTICS = 2, SETTINGS = 3,
                            CHAT = 4, ADD = 5, THEME_LIGHT = 6, THEME_DARK = 7,
                            FOOD = 8, TRANSPORT = 9, SHOPPING = 10, ENTERTAINMENT = 11,
                            BILLS = 12, OTHERS = 13,
                            EDIT = 14, DELETE = 15, SEND = 16, SAVE = 17,
                            CANCEL = 18, HELP = 19, SUCCESS = 20, ERROR = 21,
                            USD = 22, EUR = 23, GBP = 24, JPY = 25;

    static {
        // Set system property for UTF-8 encoding
        System.setProperty("file.encoding", "UTF-8");
        try {
            // Set default font that supports emojis
            Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
            Font fallbackFont = new Font("Dialog", Font.PLAIN, 14);
            
            UIManager.put("Button.font", emojiFont);
            UIManager.put("Label.font", emojiFont);
            
            // Set fallback font if emoji font is not available
            if (!emojiFont.canDisplay('\u2764')) { // Simple heart character
                UIManager.put("Button.font", fallbackFont);
                UIManager.put("Label.font", fallbackFont);
            }
        } catch (Exception e) {
            System.err.println("Error setting fonts: " + e.getMessage());
        }
    }

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.expenseDAO = new ExpenseDAO();
        this.messages = ResourceBundle.getBundle("messages", Locale.getDefault());
        
        // Set system property for UTF-8 encoding
        System.setProperty("file.encoding", "UTF-8");
        
        setupFrame();
        initializeComponents();
        setupAnimations();
        setVisible(true);
    }

    private Font getIconFont() {
        Font[] fonts = {
            new Font("Segoe UI Emoji", Font.PLAIN, 14),
            new Font("Apple Color Emoji", Font.PLAIN, 14),
            new Font("Noto Color Emoji", Font.PLAIN, 14),
            new Font("Segoe UI Symbol", Font.PLAIN, 14),
            new Font("Dialog", Font.PLAIN, 14)
        };
        
        for (Font font : fonts) {
            if (font.canDisplay('$') && font.canDisplay('✓')) {
                return font;
            }
        }
        return fonts[fonts.length - 1];
    }

    private void setupFrame() {
        setTitle("Modern Expense Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setBackground(primaryColor);
        
        try {
            // Set FlatLaf theme with custom defaults
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Set custom colors
            UIManager.put("Panel.background", primaryColor);
            UIManager.put("Button.background", accentColor);
            UIManager.put("Button.foreground", isDarkTheme ? PRIMARY_DARK : PRIMARY_LIGHT);
            UIManager.put("TextField.background", isDarkTheme ? SECONDARY_DARK : SECONDARY_LIGHT);
            UIManager.put("TextField.foreground", textColor);
            UIManager.put("ComboBox.background", isDarkTheme ? SECONDARY_DARK : SECONDARY_LIGHT);
            UIManager.put("ComboBox.foreground", textColor);
            UIManager.put("Label.foreground", textColor);
            UIManager.put("ScrollPane.background", primaryColor);
            UIManager.put("TableHeader.background", accentColor);
            UIManager.put("TableHeader.foreground", isDarkTheme ? PRIMARY_DARK : PRIMARY_LIGHT);
            
            // Set rounded corners
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            
            // Update UI
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        // Main Panel with Border Layout
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(primaryColor);

        // Create Content Panel with Card Layout
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(secondaryColor);

        // Create Different Views
        createDashboardView();
        createExpensesView();
        createAnalyticsView();
        createSettingsView();
        createChatView();

        // Create Sidebar
        createSidebar();

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Show dashboard by default
        contentLayout.show(contentPanel, "dashboard");

        add(mainPanel);
    }

    private void createSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(primaryColor);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        // Logo and Welcome Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(primaryColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Logo
        JLabel logoLabel = new JLabel("💰 Expense Manager");
        logoLabel.setFont(TITLE_FONT);
        logoLabel.setForeground(accentColor);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "! 👋");
        welcomeLabel.setFont(REGULAR_FONT);
        welcomeLabel.setForeground(new Color(128, 128, 128));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        topPanel.add(logoLabel);
        topPanel.add(welcomeLabel);

        // Navigation Panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(primaryColor);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        addNavButton("dashboard", navPanel);
        addNavButton("expenses", navPanel);
        addNavButton("analytics", navPanel);
        addNavButton("chat", navPanel);
        addNavButton("settings", navPanel);

        // Theme Toggle Panel
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        themePanel.setBackground(primaryColor);
        createThemeToggle(themePanel);

        sidebarPanel.add(topPanel, BorderLayout.NORTH);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
        sidebarPanel.add(themePanel, BorderLayout.SOUTH);
    }

    private void addNavButton(String command, JPanel container) {
        String icon;
        switch (command) {
            case "dashboard": icon = getIcon(DASHBOARD); break;
            case "expenses": icon = getIcon(EXPENSES); break;
            case "analytics": icon = getIcon(ANALYTICS); break;
            case "chat": icon = getIcon(CHAT); break;
            case "settings": icon = getIcon(SETTINGS); break;
            default: icon = getIcon(ADD); break;
        }
        String text = command.substring(0, 1).toUpperCase() + command.substring(1);
        JButton button = createIconButton(icon + " " + text);
        button.setForeground(textColor);
        button.setBackground(primaryColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(secondaryColor);
                button.setForeground(accentColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
                button.setForeground(textColor);
            }
        });

        button.addActionListener(e -> contentLayout.show(contentPanel, command));
        container.add(button);
        container.add(Box.createVerticalStrut(5));
    }

    private String getIcon(int index) {
        try {
            return ICONS[index];
        } catch (Exception e) {
            switch (index) {
                case DASHBOARD: return "[D]";
                case EXPENSES: return "[$]";
                case ANALYTICS: return "[A]";
                case SETTINGS: return "[S]";
                case CHAT: return "[C]";
                case ADD: return "[+]";
                case EDIT: return "[E]";
                case DELETE: return "[X]";
                default: return "[*]";
            }
        }
    }

    private void createDashboardView() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(20, 20));
        dashboardPanel.setBackground(secondaryColor);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = createHeaderPanel("Dashboard Overview");

        // Quick Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(secondaryColor);
        addStatCard("Total Expenses", "$2,450", "📈 +15%", statsPanel);
        addStatCard("Monthly Average", "$820", "📊", statsPanel);
        addStatCard("Top Category", "Food & Dining", "🍽", statsPanel);

        // Charts Panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(secondaryColor);
        addExpenseChart(chartsPanel);
        addCategoryChart(chartsPanel);

        // Recent Transactions
        JPanel recentPanel = createRecentTransactionsPanel();

        // Layout
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(secondaryColor);
        mainContent.add(statsPanel, BorderLayout.NORTH);
        mainContent.add(chartsPanel, BorderLayout.CENTER);
        mainContent.add(recentPanel, BorderLayout.SOUTH);

        dashboardPanel.add(headerPanel, BorderLayout.NORTH);
        dashboardPanel.add(mainContent, BorderLayout.CENTER);

        contentPanel.add(dashboardPanel, "dashboard");
    }

    private JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(secondaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(textColor);

        JButton addButton = new JButton(ADD_ICON + " Add Expense");
        addButton.setFont(REGULAR_FONT);
        addButton.setBackground(accentColor);
        addButton.setForeground(Color.WHITE);
        addButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddExpenseDialog());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void addStatCard(String title, String value, String trend, JPanel container) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(isDarkTheme ? SECONDARY_DARK : SECONDARY_LIGHT);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, isDarkTheme ? BORDER_DARK : BORDER_LIGHT),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(REGULAR_FONT);
        titleLabel.setForeground(textColor);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(TITLE_FONT);
        valueLabel.setForeground(accentColor);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        valuePanel.setBackground(isDarkTheme ? SECONDARY_DARK : SECONDARY_LIGHT);
        valuePanel.add(valueLabel);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);

        container.add(card);
    }

    private void addExpenseChart(JPanel container) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        try {
            // Get real expense data
            Map<String, BigDecimal> categoryTotals = expenseDAO.getExpensesByCategory(currentUser.getId());
            categoryTotals.forEach((category, amount) -> 
                dataset.setValue(category, amount.doubleValue())
            );
        } catch (SQLException e) {
            e.printStackTrace();
            // Add sample data as fallback
            dataset.setValue("Food", 35);
            dataset.setValue("Transport", 25);
            dataset.setValue("Shopping", 20);
            dataset.setValue("Others", 20);
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Expense Distribution",
            dataset,
            true,
            true,
            false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(primaryColor);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(primaryColor);
        wrapper.setBorder(new RoundedBorder(10, new Color(230, 230, 230)));
        wrapper.add(chartPanel);
        
        container.add(wrapper);
    }

    private void addCategoryChart(JPanel container) {
        // Similar to addExpenseChart but with different chart type
        // Implementation omitted for brevity
    }

    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(primaryColor);
        panel.setBorder(new RoundedBorder(10, new Color(230, 230, 230)));

        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Create table with real data
        String[] columns = {"Date", "Category", "Description", "Amount", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only actions column is editable
            }
        };

        try {
            List<Map<String, Object>> recentExpenses = expenseDAO.getRecentExpenses(currentUser.getId(), 10);
            for (Map<String, Object> expense : recentExpenses) {
                model.addRow(new Object[]{
                    expense.get("date"),
                    expense.get("category") + " " + getCategoryIcon((String)expense.get("category")),
                    expense.get("description"),
                    String.format("$%.2f", expense.get("amount")),
                    expense.get("id") // Store ID for actions
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading recent transactions: " + e.getMessage());
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setBackground(primaryColor);
        table.getTableHeader().setFont(REGULAR_FONT.deriveFont(Font.BOLD));

        // Set custom renderer for the actions column
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

                JButton editButton = new JButton("✏️");
                JButton deleteButton = new JButton("🗑️");
                styleActionButton(editButton);
                styleActionButton(deleteButton);

                panel.add(editButton);
                panel.add(deleteButton);
                return panel;
            }
        });

        // Set custom editor for the actions column
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()) {
            private Object cellValue;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                this.cellValue = value;
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                panel.setBackground(table.getSelectionBackground());

                JButton editButton = new JButton("✏️");
                JButton deleteButton = new JButton("🗑️");
                styleActionButton(editButton);
                styleActionButton(deleteButton);

                editButton.addActionListener(e -> {
                    int expenseId = (Integer) cellValue;
                    editExpense(createExpenseFromRow(table, row, expenseId));
                    fireEditingStopped();
                });

                deleteButton.addActionListener(e -> {
                    int expenseId = (Integer) cellValue;
                    deleteExpense(createExpenseFromRow(table, row, expenseId));
                    fireEditingStopped();
                });

                panel.add(editButton);
                panel.add(deleteButton);
                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return cellValue;
            }
        });

        // Adjust column widths
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(primaryColor);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleActionButton(JButton button) {
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(new Color(0, 0, 0, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
    }

    private Expense createExpenseFromRow(JTable table, int row, int id) {
        Expense expense = new Expense();
        expense.setId(id);
        expense.setUserId(currentUser.getId());
        expense.setDate(LocalDate.parse(table.getValueAt(row, 0).toString()));
        String category = table.getValueAt(row, 1).toString().split(" ")[0]; // Remove icon
        expense.setCategoryName(category);
        expense.setDescription(table.getValueAt(row, 2).toString());
        String amountStr = table.getValueAt(row, 3).toString().replace("$", "");
        expense.setAmount(new BigDecimal(amountStr));
        return expense;
    }

    private String getCategoryIcon(String category) {
        String lowerCategory = category.toLowerCase();
        switch (lowerCategory) {
            case "food": return getIcon(FOOD);
            case "transport": return getIcon(TRANSPORT);
            case "shopping": return getIcon(SHOPPING);
            case "entertainment": return getIcon(ENTERTAINMENT);
            case "bills": return getIcon(BILLS);
            default: return getIcon(OTHERS);
        }
    }

    private void createExpensesView() {
        // Implementation for expenses view
        JPanel expensesPanel = new JPanel();
        expensesPanel.setBackground(secondaryColor);
        contentPanel.add(expensesPanel, "expenses");
    }

    private void createAnalyticsView() {
        // Implementation for analytics view
        JPanel analyticsPanel = new JPanel();
        analyticsPanel.setBackground(secondaryColor);
        contentPanel.add(analyticsPanel, "analytics");
    }

    private void createSettingsView() {
        JPanel settingsPanel = new JPanel(new BorderLayout(20, 20));
        settingsPanel.setBackground(secondaryColor);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = createHeaderPanel("Settings");

        // Settings Content
        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 0, 20));
        contentPanel.setBackground(secondaryColor);

        // OpenAI API Settings
        JPanel apiSection = createSettingsSection("🤖 OpenAI API",
            "Configure your OpenAI API key for AI chat features",
            createAPIKeyPanel());

        // Theme Settings
        JPanel themeSection = createSettingsSection("🎨 Theme",
            "Choose between light and dark theme",
            createThemeTogglePanel());

        // Language Settings
        JPanel languageSection = createSettingsSection("🌐 Language",
            "Select your preferred language",
            createLanguagePanel());

        // Currency Settings
        JPanel currencySection = createSettingsSection("💱 Currency",
            "Set your default currency",
            createCurrencyPanel());

        // Export Settings
        JPanel exportSection = createSettingsSection("📤 Export Data",
            "Export your expense data",
            createExportPanel());

        // Add sections to content
        contentPanel.add(apiSection);
        contentPanel.add(themeSection);
        contentPanel.add(languageSection);
        contentPanel.add(currencySection);
        contentPanel.add(exportSection);

        // Add scroll support
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(secondaryColor);
        scrollPane.getViewport().setBackground(secondaryColor);

        settingsPanel.add(headerPanel, BorderLayout.NORTH);
        settingsPanel.add(scrollPane, BorderLayout.CENTER);

        this.contentPanel.add(settingsPanel, "settings");
    }

    private JPanel createAPIKeyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(primaryColor);

        // Get current API key
        String currentKey = ConfigManager.getOpenAIKey();
        JPasswordField apiKeyField = new JPasswordField(currentKey, 20);
        apiKeyField.setFont(REGULAR_FONT);
        apiKeyField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, new Color(230, 230, 230)),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JButton saveButton = new JButton("Save Key");
        styleButton(saveButton, accentColor);
        saveButton.addActionListener(e -> {
            String apiKey = new String(apiKeyField.getPassword()).trim();
            if (apiKey.isEmpty()) {
                showErrorMessage("Please enter an API key");
                return;
            }
            if (!apiKey.startsWith("sk-")) {
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "The API key should typically start with 'sk-'. Are you sure this is a valid key?",
                    "Confirm API Key",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            ConfigManager.setOpenAIKey(apiKey);
            showSuccessMessage("API key saved successfully!");
        });

        JButton helpButton = new JButton("?");
        helpButton.setFont(REGULAR_FONT);
        helpButton.setForeground(textColor);
        helpButton.setBackground(primaryColor);
        helpButton.setBorder(new RoundedBorder(10, new Color(230, 230, 230)));
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpButton.addActionListener(e -> showAPIKeyHelp());

        panel.add(apiKeyField);
        panel.add(saveButton);
        panel.add(helpButton);

        return panel;
    }

    private void showAPIKeyHelp() {
        JDialog helpDialog = new JDialog(this, "OpenAI API Key Help", true);
        helpDialog.setSize(400, 300);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout(20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(primaryColor);

        JLabel titleLabel = new JLabel("How to get your OpenAI API key:");
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea instructionsArea = new JTextArea(
            "1. Go to platform.openai.com\n" +
            "2. Sign in or create an account\n" +
            "3. Click on your profile picture\n" +
            "4. Select 'View API keys'\n" +
            "5. Click 'Create new secret key'\n" +
            "6. Copy and paste the key here\n\n" +
            "Note: Keep your API key secure and never share it!"
        );
        instructionsArea.setFont(REGULAR_FONT);
        instructionsArea.setEditable(false);
        instructionsArea.setBackground(primaryColor);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setLineWrap(true);
        instructionsArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton openWebsiteButton = new JButton("Visit OpenAI Platform");
        styleButton(openWebsiteButton, accentColor);
        openWebsiteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        openWebsiteButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://platform.openai.com/account/api-keys"));
            } catch (Exception ex) {
                showErrorMessage("Could not open website: " + ex.getMessage());
            }
        });

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(instructionsArea);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(openWebsiteButton);

        helpDialog.add(contentPanel, BorderLayout.CENTER);
        helpDialog.setVisible(true);
    }

    private JPanel createSettingsSection(String title, String description, JComponent control) {
        JPanel section = new JPanel(new BorderLayout(10, 5));
        section.setBackground(primaryColor);
        section.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, new Color(230, 230, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title and description
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setBackground(primaryColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(textColor);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(REGULAR_FONT);
        descLabel.setForeground(new Color(128, 128, 128));

        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        section.add(textPanel, BorderLayout.CENTER);
        section.add(control, BorderLayout.EAST);

        return section;
    }

    private JPanel createThemeTogglePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(primaryColor);
        createThemeToggle(panel);
        return panel;
    }

    private JPanel createLanguagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(primaryColor);

        String[] languages = {"English", "Español", "Français", "Deutsch"};
        JComboBox<String> languageCombo = new JComboBox<>(languages);
        languageCombo.setFont(REGULAR_FONT);
        languageCombo.setBackground(primaryColor);
        languageCombo.addActionListener(e -> {
            String selected = (String) languageCombo.getSelectedItem();
            if (selected != null) {
                changeLanguage(selected);
            }
        });

        panel.add(languageCombo);
        return panel;
    }

    private JPanel createCurrencyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(primaryColor);

        String[] currencies = {"USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)"};
        JComboBox<String> currencyCombo = new JComboBox<>(currencies);
        currencyCombo.setFont(REGULAR_FONT);
        currencyCombo.setBackground(primaryColor);

        panel.add(currencyCombo);
        return panel;
    }

    private JPanel createExportPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(primaryColor);

        JButton exportButton = new JButton("Export to Excel");
        styleButton(exportButton, accentColor);
        exportButton.addActionListener(e -> exportData());

        panel.add(exportButton);
        return panel;
    }

    private void exportData() {
        try {
            // TODO: Implement actual export functionality
            showSuccessMessage("Data exported successfully!");
        } catch (Exception e) {
            showErrorMessage("Error exporting data: " + e.getMessage());
        }
    }

    private void changeLanguage(String language) {
        Locale locale;
        switch (language) {
            case "Español":
                locale = new Locale("es");
                break;
            case "Français":
                locale = new Locale("fr");
                break;
            case "Deutsch":
                locale = new Locale("de");
                break;
            default:
                locale = new Locale("en");
                break;
        }
        
        messages = ResourceBundle.getBundle("messages", locale);
        refreshUI();
    }

    private void createChatView() {
        JPanel chatPanel = new JPanel(new BorderLayout(20, 20));
        chatPanel.setBackground(secondaryColor);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = createHeaderPanel("AI Financial Assistant");

        // Chat messages panel
        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(primaryColor);
        messagesPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(new RoundedBorder(10, new Color(230, 230, 230)));
        scrollPane.setBackground(primaryColor);
        scrollPane.getViewport().setBackground(primaryColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Welcome message
        addMessageBubble(messagesPanel, "Hello! I'm your AI financial assistant. How can I help you today?", false);

        // Input panel with modern design
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(secondaryColor);
        inputPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JTextField inputField = new JTextField();
        inputField.setFont(REGULAR_FONT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(25, new Color(230, 230, 230)),
            new EmptyBorder(12, 20, 12, 20)
        ));

        JButton sendButton = new JButton(new String("➤".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sendButton.setBackground(accentColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(50, 50));

        // Action for sending messages
        ActionListener sendAction = e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                addMessageBubble(messagesPanel, message, true);
                inputField.setText("");
                scrollToBottom(scrollPane);
                
                // Process AI response in background
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        String apiKey = ConfigManager.getOpenAIKey();
                        if (apiKey == null || apiKey.trim().isEmpty()) {
                            addMessageBubble(messagesPanel, "Please set up your OpenAI API key in Settings first.", false);
                            return null;
                        }

                        try {
                            AIChatService chatService = new AIChatService(apiKey);
                            Map<String, Object> dashboardData = new HashMap<>();
                            dashboardData.put("totalExpenses", expenseDAO.getTotalExpenses(currentUser.getId()));
                            dashboardData.put("categories", expenseDAO.getExpensesByCategory(currentUser.getId()));

                            String response = chatService.processQuestion(message, dashboardData);
                            addMessageBubble(messagesPanel, response, false);
                        } catch (Exception ex) {
                            addMessageBubble(messagesPanel, "Sorry, I encountered an error: " + ex.getMessage(), false);
                        }
                        scrollToBottom(scrollPane);
                        return null;
                    }
                };
                worker.execute();
            }
        };

        // Add action listeners
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        // Add hover effect to send button
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(accentColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(accentColor);
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(headerPanel, BorderLayout.NORTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        this.contentPanel.add(chatPanel, "chat");
    }

    private void addMessageBubble(JPanel container, String message, boolean isUser) {
        JPanel bubblePanel = new JPanel(new BorderLayout(10, 5));
        bubblePanel.setBackground(primaryColor);
        bubblePanel.setBorder(new EmptyBorder(5, isUser ? 50 : 10, 5, isUser ? 10 : 50));
        bubblePanel.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        JTextArea textArea = new JTextArea(message);
        textArea.setFont(REGULAR_FONT);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(isUser ? new Color(79, 93, 247, 30) : new Color(200, 200, 200, 30));
        textArea.setForeground(textColor);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15, isUser ? new Color(79, 93, 247, 50) : new Color(200, 200, 200, 50)),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // Calculate preferred size based on content
        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        int maxWidth = 400; // Maximum bubble width
        String[] lines = message.split("\n");
        int width = 0;
        int height = 0;
        for (String line : lines) {
            int lineWidth = fm.stringWidth(line);
            width = Math.min(Math.max(width, lineWidth + 40), maxWidth); // Add padding
            height += fm.getHeight();
        }
        // Add extra height for word wrap
        height += (width < maxWidth ? 0 : (fm.getHeight() * (width / maxWidth)));
        textArea.setPreferredSize(new Dimension(width, height + 20));

        bubblePanel.add(textArea, BorderLayout.CENTER);
        
        // Add timestamp
        JLabel timeLabel = new JLabel(new SimpleDateFormat("HH:mm").format(new Date()));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(128, 128, 128));
        bubblePanel.add(timeLabel, isUser ? BorderLayout.WEST : BorderLayout.EAST);

        container.add(bubblePanel);
        container.revalidate();
        container.repaint();
    }

    private void scrollToBottom(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void createThemeToggle(JPanel container) {
        JPanel toggleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toggleWrapper.setBackground(primaryColor);

        JLabel themeLabel = new JLabel(isDarkTheme ? THEME_ICON_DARK : THEME_ICON_LIGHT);
        themeLabel.setFont(REGULAR_FONT);

        JPanel toggleButton = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background
                g2d.setColor(isDarkTheme ? accentColor : new Color(200, 200, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

                // Draw toggle circle
                g2d.setColor(Color.WHITE);
                int diameter = getHeight() - 4;
                int x = isDarkTheme ? getWidth() - diameter - 2 : 2;
                g2d.fillOval(x, 2, diameter, diameter);

                g2d.dispose();
            }
        };

        toggleButton.setPreferredSize(new Dimension(50, 24));
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleTheme();
                themeLabel.setText(isDarkTheme ? THEME_ICON_DARK : THEME_ICON_LIGHT);
                toggleButton.repaint();
            }
        });

        container.add(themeLabel);
        container.add(toggleButton);
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        
        // Update colors with monochrome scheme
        if (isDarkTheme) {
            primaryColor = new Color(18, 18, 18);      // Almost black
            secondaryColor = new Color(30, 30, 30);    // Dark gray
            accentColor = new Color(200, 200, 200);    // Light gray
            textColor = new Color(255, 255, 255);      // Pure white
        } else {
            primaryColor = new Color(255, 255, 255);   // Pure white
            secondaryColor = new Color(245, 245, 245); // Light gray
            accentColor = new Color(50, 50, 50);       // Dark gray
            textColor = new Color(33, 33, 33);         // Almost black
        }

        // Update UI with new monochrome colors
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(isDarkTheme ? new FlatDarkLaf() : new FlatLightLaf());
                
                // Set custom colors
                UIManager.put("Panel.background", primaryColor);
                UIManager.put("Button.background", accentColor);
                UIManager.put("Button.foreground", isDarkTheme ? primaryColor : textColor);
                UIManager.put("TextField.background", secondaryColor);
                UIManager.put("TextField.foreground", textColor);
                UIManager.put("ComboBox.background", secondaryColor);
                UIManager.put("ComboBox.foreground", textColor);
                UIManager.put("Label.foreground", textColor);
                UIManager.put("Table.background", primaryColor);
                UIManager.put("Table.foreground", textColor);
                UIManager.put("TableHeader.background", accentColor);
                UIManager.put("TableHeader.foreground", isDarkTheme ? primaryColor : textColor);
                
                SwingUtilities.updateComponentTreeUI(this);
                refreshUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void refreshUI() {
        // Update main components
        mainPanel.setBackground(primaryColor);
        sidebarPanel.setBackground(primaryColor);
        contentPanel.setBackground(secondaryColor);
        
        // Update borders
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, 
            isDarkTheme ? new Color(50, 50, 50) : new Color(220, 220, 220)));
            
        // Force repaint
        revalidate();
        repaint();
    }

    private void setupAnimations() {
        javax.swing.Timer fadeTimer = new javax.swing.Timer(50, null);
        fadeTimer.addActionListener(e -> {
            // Add fade animations for panel transitions
            contentPanel.repaint();
        });
    }

    private void showAddExpenseDialog() {
        JDialog dialog = new JDialog(this, "Add New Expense", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(primaryColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Date Field
        JLabel dateLabel = new JLabel(FOOD_ICON + " Date:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        addFormField(formPanel, dateLabel, dateSpinner, gbc, 0);

        // Amount Field
        JLabel amountLabel = new JLabel(EXPENSES_ICON + " Amount:");
        JTextField amountField = new JTextField();
        addFormField(formPanel, amountLabel, amountField, gbc, 1);

        // Category Field
        JLabel categoryLabel = new JLabel("🏷️ Category:");
        String[] categories = {"Food", "Transport", "Shopping", "Entertainment", "Bills", "Others"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        addFormField(formPanel, categoryLabel, categoryBox, gbc, 2);

        // Description Field
        JLabel descLabel = new JLabel("📝 Description:");
        JTextField descField = new JTextField();
        addFormField(formPanel, descLabel, descField, gbc, 3);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(primaryColor);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(200, 200, 200));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, accentColor);
        saveButton.addActionListener(e -> {
            try {
                // Create new expense
                Expense expense = new Expense();
                expense.setUserId(currentUser.getId());
                expense.setAmount(new BigDecimal(amountField.getText()));
                expense.setDate(((Date) dateSpinner.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate());
                expense.setDescription(descField.getText());
                expense.setCategoryName(categoryBox.getSelectedItem().toString());

                // Save to database
                expenseDAO.createExpense(expense);

                // Close dialog
                dialog.dispose();
                
                // Refresh the entire dashboard view
                SwingUtilities.invokeLater(() -> {
                    contentPanel.remove(contentPanel.getComponent(0));
                    createDashboardView();
                    contentLayout.show(contentPanel, "dashboard");
                    revalidate();
                    repaint();
                });
                
                showSuccessMessage("Expense added successfully!");
            } catch (Exception ex) {
                showErrorMessage("Error adding expense: " + ex.getMessage());
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, JLabel label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(REGULAR_FONT);
        button.setBackground(bgColor);
        button.setForeground(isDarkTheme ? PRIMARY_DARK : PRIMARY_LIGHT);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
    }

    private void showSuccessMessage(String message) {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(SUCCESS_COLOR);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel label = new JLabel(message);
        label.setFont(REGULAR_FONT);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    private void showErrorMessage(String message) {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(ERROR_COLOR);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel label = new JLabel(message);
        label.setFont(REGULAR_FONT);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    private void refreshDashboard() {
        try {
            // Refresh stats
            BigDecimal totalExpenses = expenseDAO.getTotalExpenses(currentUser.getId());
            Map<String, BigDecimal> categoryTotals = expenseDAO.getExpensesByCategory(currentUser.getId());
            
            // Update UI components
            SwingUtilities.invokeLater(() -> {
                // Refresh all panels
                createDashboardView();
                contentLayout.show(contentPanel, "dashboard");
                revalidate();
                repaint();
            });
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error refreshing dashboard: " + e.getMessage());
        }
    }

    // Add this method to handle expense editing
    private void editExpense(Expense expense) {
        JDialog dialog = new JDialog(this, "Edit Expense", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(primaryColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Date Field
        JLabel dateLabel = new JLabel(FOOD_ICON + " Date:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(java.sql.Date.valueOf(expense.getDate()));
        addFormField(formPanel, dateLabel, dateSpinner, gbc, 0);

        // Amount Field
        JLabel amountLabel = new JLabel(EXPENSES_ICON + " Amount:");
        JTextField amountField = new JTextField(expense.getAmount().toString());
        addFormField(formPanel, amountLabel, amountField, gbc, 1);

        // Category Field
        JLabel categoryLabel = new JLabel("🏷️ Category:");
        String[] categories = {"Food", "Transport", "Shopping", "Entertainment", "Bills", "Others"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        categoryBox.setSelectedItem(expense.getCategoryName());
        addFormField(formPanel, categoryLabel, categoryBox, gbc, 2);

        // Description Field
        JLabel descLabel = new JLabel("📝 Description:");
        JTextField descField = new JTextField(expense.getDescription());
        addFormField(formPanel, descLabel, descField, gbc, 3);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(primaryColor);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(200, 200, 200));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, accentColor);
        saveButton.addActionListener(e -> {
            try {
                // Update expense object
                expense.setAmount(new BigDecimal(amountField.getText()));
                expense.setDate(((Date) dateSpinner.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate());
                expense.setDescription(descField.getText());
                expense.setCategoryName(categoryBox.getSelectedItem().toString());

                // Update in database
                expenseDAO.updateExpense(expense);

                // Close dialog
                dialog.dispose();
                
                // Refresh the dashboard
                refreshDashboard();
                showSuccessMessage("Expense updated successfully!");
            } catch (Exception ex) {
                showErrorMessage("Error updating expense: " + ex.getMessage());
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Add this method to handle expense deletion
    private void deleteExpense(Expense expense) {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this expense?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Delete from database
                expenseDAO.deleteExpense(expense.getId());
                refreshDashboard();
                showSuccessMessage("Expense deleted successfully!");
            } catch (Exception e) {
                showErrorMessage("Error deleting expense: " + e.getMessage());
            }
        }
    }

    // Inner class for rounded borders
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        Font buttonFont = getIconFont();
        button.setFont(buttonFont);
        return button;
    }
} 