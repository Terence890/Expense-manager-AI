/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.expensemanager.utils;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author Terence
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            try {
                // Create default config file if it doesn't exist
                configFile.createNewFile();
                properties.setProperty("deepseek.api.key", "");
                saveConfig("Initial configuration created");
            } catch (IOException e) {
                System.err.println("Error creating config file: " + e.getMessage());
            }
        } else {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading config: " + e.getMessage());
            }
        }
    }
    
    private static void saveConfig(String comment) {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, comment);
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
            throw new RuntimeException("Failed to save configuration", e);
        }
    }
    
    public static String getDeepseekAIKey() {
        // First try environment variable
        String key = System.getenv("DEEPSEEK_API_KEY");
        if (key != null && !key.trim().isEmpty()) {
            return key.trim();
        }
        
        // Then try properties file
        key = properties.getProperty("deepseek.api.key");
        return key != null ? key.trim() : "";
    }
    
    public static void setDeepseekAIKey(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("API key cannot be null");
        }
        properties.setProperty("deepseek.api.key", apiKey.trim());
        saveConfig("Updated Deepseek AI API key");
    }
    
    // Deprecated: Will be removed in future versions
    @Deprecated
    public static String getOpenAIKey() {
        // First try environment variable
        String key = System.getenv("OPENAI_API_KEY");
        if (key != null && !key.trim().isEmpty()) {
            return key;
        }
        
        // Then try properties file
        return properties.getProperty("openai.api.key");
    }
    
    // Deprecated: Will be removed in future versions
    @Deprecated
    public static void setOpenAIKey(String apiKey) {
        properties.setProperty("openai.api.key", apiKey);
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Expense Manager Configuration");
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }
}
