package com.expensemanager.ui;

import com.expensemanager.utils.AIChatService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChatPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color ACCENT_COLOR = new Color(63, 81, 181);
    private static final Color USER_BUBBLE_COLOR = new Color(63, 81, 181);
    private static final Color AI_BUBBLE_COLOR = new Color(240, 240, 240);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int BORDER_RADIUS = 10;

    private final ResourceBundle messages;
    private final AIChatService chatService;
    private Map<String, Object> dashboardData;
    private JTextField inputField;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;

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
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
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

    public ChatPanel(ResourceBundle messages, String apiKey) {
        this.messages = messages;
        try {
            this.chatService = new AIChatService(apiKey);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Error initializing AI Chat: " + e.getMessage(),
                "Configuration Error",
                JOptionPane.ERROR_MESSAGE);
            throw e;
        }
        this.dashboardData = new HashMap<>();
        initializeUI();
    }

    public void updateDashboardData(Map<String, Object> data) {
        this.dashboardData = new HashMap<>(data);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Messages Panel
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(BACKGROUND_COLOR);

        // Scroll Pane
        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        inputField.setFont(REGULAR_FONT);
        inputField.setBorder(new RoundedBorder(BORDER_RADIUS, ACCENT_COLOR));
        inputField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.setFont(REGULAR_FONT);
        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(new RoundedBorder(BORDER_RADIUS, ACCENT_COLOR));
        sendButton.addActionListener(e -> sendMessage());
        sendButton.setPreferredSize(new Dimension(80, 30));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        // Add user message
        addMessage(message, true);
        inputField.setText("");

        // Process with AI in background
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    return chatService.processQuestion(message, dashboardData);
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    addMessage(response, false);
                } catch (ExecutionException e) {
                    addMessage("Error processing request: " + e.getMessage(), false);
                } catch (Exception e) {
                    addMessage("Error processing request: " + e.getMessage(), false);
                }
            }
        };
        worker.execute();
    }

    private void addMessage(String message, boolean isUser) {
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BorderLayout());
        bubblePanel.setBackground(BACKGROUND_COLOR);

        JTextArea textArea = new JTextArea(message);
        textArea.setFont(REGULAR_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(isUser ? USER_BUBBLE_COLOR : AI_BUBBLE_COLOR);
        textArea.setForeground(isUser ? Color.WHITE : TEXT_PRIMARY);
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND_COLOR);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, isUser ? 50 : 10, 5, isUser ? 10 : 50));
        wrapper.add(textArea);

        bubblePanel.add(wrapper, isUser ? BorderLayout.EAST : BorderLayout.WEST);
        messagesPanel.add(bubblePanel);

        // Ensure the latest message is visible
        messagesPanel.revalidate();
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}