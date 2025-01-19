package com.expensemanager.ui;

import com.expensemanager.dao.UserDAO;
import com.expensemanager.models.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private final UserDAO userDAO;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;
    private CardLayout cardLayout;
    private Timer shakeTimer;
    private int shakeCount;
    private final int SHAKE_DISTANCE = 10;
    private Font iconFont;

    public LoginFrame() {
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Expense Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 500, 20, 20));

        // Main panel with card layout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Create panels
        createLoginPanel();
        createRegisterPanel();

        // Add panels to card layout
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");

        // Add main panel to frame
        add(mainPanel);

        // Initialize shake animation
        shakeTimer = new Timer(50, e -> {
            if (shakeCount < 10) {
                Point p = getLocation();
                setLocation(p.x + (shakeCount % 2 == 0 ? SHAKE_DISTANCE : -SHAKE_DISTANCE), p.y);
                shakeCount++;
            } else {
                shakeTimer.stop();
                setLocation(getX() + SHAKE_DISTANCE, getY());
                shakeCount = 0;
            }
        });

        // Make window draggable
        addDraggableMouseListener();

        // Load custom font
        loadCustomFont();

        // Setup icons
        setupIcons();
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(null);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Close button
        JButton closeButton = createIconButton("×", 360, 10, 30, 30);
        closeButton.addActionListener(e -> System.exit(0));
        loginPanel.add(closeButton);

        // App icon and title
        JLabel iconLabel = new JLabel("💰", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setBounds(150, 50, 100, 60);
        loginPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Expense Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219));
        titleLabel.setBounds(100, 120, 200, 30);
        loginPanel.add(titleLabel);

        // Username field
        JLabel userLabel = new JLabel("👤 Username");
        userLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        userLabel.setBounds(50, 180, 300, 20);
        loginPanel.add(userLabel);

        usernameField = createStyledTextField();
        usernameField.setBounds(50, 205, 300, 40);
        loginPanel.add(usernameField);

        // Password field
        JLabel passLabel = new JLabel("🔒 Password");
        passLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        passLabel.setBounds(50, 260, 300, 20);
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordField.setBounds(50, 285, 300, 40);
        loginPanel.add(passwordField);

        // Login button
        loginButton = createStyledButton("LOGIN", new Color(52, 152, 219));
        loginButton.setBounds(50, 350, 300, 45);
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton);

        // Register link
        JLabel registerLabel = new JLabel("Don't have an account? Register here", SwingConstants.CENTER);
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerLabel.setForeground(new Color(52, 152, 219));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.setBounds(50, 410, 300, 30);
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                animateTransition("register");
            }
        });
        loginPanel.add(registerLabel);
    }

    private void createRegisterPanel() {
        registerPanel = new JPanel(null);
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Close button
        JButton closeButton = createIconButton("×", 360, 10, 30, 30);
        closeButton.addActionListener(e -> System.exit(0));
        registerPanel.add(closeButton);

        // Back button
        JButton backButton = createIconButton("←", 10, 10, 30, 30);
        backButton.addActionListener(e -> animateTransition("login"));
        registerPanel.add(backButton);

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219));
        titleLabel.setBounds(100, 60, 200, 30);
        registerPanel.add(titleLabel);

        // Register form fields
        JTextField regUsernameField = createStyledTextField();
        JPasswordField regPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        styleTextField(regPasswordField);
        styleTextField(confirmPasswordField);

        // Username
        JLabel userLabel = new JLabel("👤 Username");
        userLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        userLabel.setBounds(50, 120, 300, 20);
        registerPanel.add(userLabel);
        regUsernameField.setBounds(50, 145, 300, 40);
        registerPanel.add(regUsernameField);

        // Password
        JLabel passLabel = new JLabel("🔒 Password");
        passLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        passLabel.setBounds(50, 200, 300, 20);
        registerPanel.add(passLabel);
        regPasswordField.setBounds(50, 225, 300, 40);
        registerPanel.add(regPasswordField);

        // Confirm Password
        JLabel confirmLabel = new JLabel("🔒 Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        confirmLabel.setBounds(50, 280, 300, 20);
        registerPanel.add(confirmLabel);
        confirmPasswordField.setBounds(50, 305, 300, 40);
        registerPanel.add(confirmPasswordField);

        // Register button
        registerButton = createStyledButton("REGISTER", new Color(46, 204, 113));
        registerButton.setBounds(50, 380, 300, 45);
        registerButton.addActionListener(e -> {
            String username = regUsernameField.getText();
            String password = new String(regPasswordField.getPassword());
            String confirmPass = new String(confirmPasswordField.getPassword());

            if (password.equals(confirmPass)) {
                try {
                    userDAO.createUser(username, password);
                    JOptionPane.showMessageDialog(this, "Registration successful! ✅");
                    animateTransition("login");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage() + " ❌");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Passwords don't match! ❌");
            }
        });
        registerPanel.add(registerButton);
    }

    private void handleLogin() {
        try {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (userDAO.authenticate(username, password)) {
                User user = userDAO.findByUsername(username);
                dispose();
                new DashboardFrame(user);
            } else {
                shakeTimer.start();
                JOptionPane.showMessageDialog(this, "Invalid username or password! ❌");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage() + " ❌");
        }
    }

    private void animateTransition(String targetCard) {
        Timer timer = new Timer(1, new ActionListener() {
            float alpha = 1.0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.1f;
                if (alpha <= 0) {
                    ((Timer)e.getSource()).stop();
                    cardLayout.show(mainPanel, targetCard);
                    startFadeIn();
                } else {
                    mainPanel.setOpaque(false);
                    Graphics2D g2d = (Graphics2D) mainPanel.getGraphics();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    mainPanel.paintAll(g2d);
                }
            }
        });
        timer.start();
    }

    private void startFadeIn() {
        Timer timer = new Timer(1, new ActionListener() {
            float alpha = 0.0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.1f;
                if (alpha >= 1) {
                    ((Timer)e.getSource()).stop();
                } else {
                    mainPanel.setOpaque(true);
                    Graphics2D g2d = (Graphics2D) mainPanel.getGraphics();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    mainPanel.paintAll(g2d);
                }
            }
        });
        timer.start();
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private JButton createIconButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setForeground(new Color(52, 152, 219));
        button.setBackground(Color.WHITE);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(41, 128, 185));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(52, 152, 219));
            }
        });

        return button;
    }

    private void addDraggableMouseListener() {
        Point offset = new Point();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                offset.setLocation(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - offset.x, p.y + e.getY() - offset.y);
            }
        });
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/FontAwesome6Free-Solid-900.otf");
            iconFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(iconFont);
        } catch (Exception e) {
            e.printStackTrace();
            iconFont = new Font("Arial", Font.PLAIN, 24); // Fallback font
        }
    }

    private void setupIcons() {
        // Icons are now handled directly in createLoginPanel() and createRegisterPanel()
    }
} 