package com.expensemanager.utils;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AIChatService {
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String SYSTEM_PROMPT = "You are a helpful financial assistant. Analyze the expense data and provide clear, concise summaries and insights.";
    private final String apiKey;
    private final OkHttpClient client;
    private final List<JSONObject> conversationHistory;
    private final MediaType JSON = MediaType.get("application/json");

    public AIChatService(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Deepseek API key cannot be null or empty");
        }
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.conversationHistory = new ArrayList<>();
        initializeConversation();
    }

    private void initializeConversation() {
        conversationHistory.clear();
        conversationHistory.add(new JSONObject()
            .put("role", "system")
            .put("content", SYSTEM_PROMPT));
    }

    public String processQuestion(String question, Map<String, Object> dashboardData) throws IOException {
        int maxRetries = 3;
        int baseDelayMs = 5000; // 5 seconds base delay

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    Thread.sleep(baseDelayMs * (long)Math.pow(2, attempt - 1));
                }
                return makeDeepseekAPIRequest(question, dashboardData);
            } catch (IOException e) {
                if (e.getMessage().contains("429") && attempt < maxRetries - 1) {
                    continue;
                }
                throw new IOException("Deepseek API error: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Request interrupted", e);
            }
        }
        throw new IOException("Max retries exceeded");
    }

    private String makeDeepseekAPIRequest(String question, Map<String, Object> dashboardData) throws IOException {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }

        // Add user message to conversation history
        JSONObject userMessage = new JSONObject()
            .put("role", "user")
            .put("content", formatQuestionWithData(question, dashboardData));
        conversationHistory.add(userMessage);

        // Prepare the request body
        JSONObject requestBody = new JSONObject()
            .put("model", "deepseek-chat")
            .put("messages", new JSONArray(conversationHistory))
            .put("temperature", 0.7)
            .put("max_tokens", 1000);

        // Build the request
        Request request = new Request.Builder()
            .url(DEEPSEEK_API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(requestBody.toString(), JSON))
            .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            
            if (!response.isSuccessful()) {
                String errorMessage = "Deepseek API error: " + response.code();
                if (responseBody != null) {
                    try {
                        JSONObject errorJson = new JSONObject(responseBody);
                        errorMessage += " - " + errorJson.optString("error", "Unknown error");
                    } catch (Exception e) {
                        errorMessage += " - " + responseBody;
                    }
                }
                throw new IOException(errorMessage);
            }

            if (responseBody == null) {
                throw new IOException("Empty response from Deepseek API");
            }

            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                String assistantResponse = jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

                // Add assistant's response to conversation history
                conversationHistory.add(new JSONObject()
                    .put("role", "assistant")
                    .put("content", assistantResponse));

                return assistantResponse;
            } catch (Exception e) {
                throw new IOException("Failed to parse Deepseek API response: " + e.getMessage());
            }
        }
    }

    private String formatQuestionWithData(String question, Map<String, Object> data) {
        StringBuilder context = new StringBuilder("Current Dashboard Data:\n");
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            context.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return context.toString() + "\n\nUser Question: " + question;
    }

    public void clearConversation() {
        initializeConversation();
    }
}