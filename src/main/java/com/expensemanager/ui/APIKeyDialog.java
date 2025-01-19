package com.expensemanager.ui;

import com.expensemanager.utils.ConfigManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Desktop;
import java.net.URI;

public class APIKeyDialog extends JDialog {
    private final JTextField apiKeyField;
    private boolean confirmed = false;

    public APIKeyDialog(Window owner) {
        super(owner, "Set Deepseek AI API Key", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Initialize components
        apiKeyField = new JTextField(30);
        initializeComponents();
        
        // Set minimum size
        setMinimumSize(new Dimension(450, 250));
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add instructions
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        
        // Create clickable link
        JLabel linkLabel = new JLabel("<html>To use the AI chat feature, you need to provide a Deepseek AI API key.<br><br>" +
            "1. Go to <a href='https://platform.deepseek.ai/account/api-keys'>platform.deepseek.ai/account/api-keys</a><br>" +
            "2. Create a new API key<br>" +
            "3. Copy and paste it here</html>");
            
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://platform.deepseek.ai/account/api-keys"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(APIKeyDialog.this,
                        "Could not open the link. Please visit the URL manually.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        mainPanel.add(linkLabel, gbc);

        // Add API key field
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 5, 5);
        mainPanel.add(new JLabel("API Key:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String currentKey = ConfigManager.getDeepseekAIKey();
        if (currentKey != null) {
            apiKeyField.setText(currentKey);
        }
        mainPanel.add(apiKeyField, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // Set default button
        getRootPane().setDefaultButton(okButton);

        okButton.addActionListener(e -> {
            String apiKey = apiKeyField.getText().trim();
            if (apiKey.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter an API key.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                apiKeyField.requestFocus();
                return;
            }
            if (!apiKey.startsWith("sk-")) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "The API key should typically start with 'sk-'. Are you sure this is a valid API key?",
                    "Confirm API Key",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) {
                    apiKeyField.requestFocus();
                    return;
                }
            }
            ConfigManager.setDeepseekAIKey(apiKey);
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        // Handle Escape key
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public static boolean showDialog(Window owner) {
        APIKeyDialog dialog = new APIKeyDialog(owner);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
} 