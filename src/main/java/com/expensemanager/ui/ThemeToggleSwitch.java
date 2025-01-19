package com.expensemanager.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ThemeToggleSwitch extends JToggleButton {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    private static final Color TOGGLE_ON = new Color(46, 204, 113);
    private static final Color TOGGLE_OFF = new Color(189, 195, 199);
    private static final Color THUMB_COLOR = Color.WHITE;
    
    public ThemeToggleSwitch() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(TOGGLE_OFF);
        setFocusPainted(false);
        setBorderPainted(false);
        
        addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setBackground(TOGGLE_ON);
            } else {
                setBackground(TOGGLE_OFF);
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
        
        // Calculate thumb position
        int thumbDiameter = getHeight() - 4;
        int thumbX = isSelected() ? getWidth() - thumbDiameter - 2 : 2;
        
        // Paint thumb
        g2.setColor(THUMB_COLOR);
        g2.fill(new Ellipse2D.Double(thumbX, 2, thumbDiameter, thumbDiameter));
        
        g2.dispose();
    }
} 