package com.habit.tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habit.tracker.model.Journal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AI_Service {

    // Pulls your secret key from application.properties
    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Journal analyzeAndParseEntry(String rawText) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        // 1. We tell Gemini exactly how to format the data
        String promptText = "You are an intelligent 'Second Brain' assistant. " +
            "The user has provided the following input: '" + rawText + "'. " +
            "Follow these rules: " +
            "1. If the input is a command or request (e.g., 'Write a note about X', 'Summarize Y', 'Give me 5 ideas for Z'), you MUST act as an AI assistant, fulfill the request completely, and generate the final content. " +
            "2. If the input is just a daily log or raw data (e.g., 'Benched 225', 'Feeling tired today'), clean it up, fix the grammar, and format it nicely. " +
            "3. Analyze the final content and categorize it. " +
            "Respond ONLY with a valid JSON object containing exactly two keys: " +
            "'type' (choose either 'journal' for short logs/metrics, or 'notebook' for generated essays/deep thoughts), and " +
            "'content' (the final generated text or formatted log). Do not include markdown formatting like ```json.";

        // 2. Build the exact JSON structure Google's API expects
        Map<String, Object> part = new HashMap<>();
        part.put("text", promptText);
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // 3. Send the request to Google
            String responseStr = restTemplate.postForObject(apiUrl, request, String.class);
            
            // 4. Drill down into Google's response to get the text we care about
            JsonNode rootNode = objectMapper.readTree(responseStr);
            String aiJsonString = rootNode.path("candidates").get(0)
                                          .path("content").path("parts").get(0)
                                          .path("text").asText();

            // 5. Clean any accidental markdown and map it to a temporary Journal object
            aiJsonString = aiJsonString.replace("```json", "").replace("```", "").trim();
            JsonNode parsedData = objectMapper.readTree(aiJsonString);

            Journal smartJournal = new Journal();
            smartJournal.setType(parsedData.path("type").asText("journal"));
            smartJournal.setContent(parsedData.path("content").asText(rawText));
            smartJournal.setDetails(parsedData.path("details").asText(""));
            
            return smartJournal;

        } catch (Exception e) {
            // If the AI is down or fails, we fail gracefully by saving exactly what the user typed.
            System.err.println("AI Parsing Failed. Falling back to raw text. Error: " + e.getMessage());
            Journal fallback = new Journal();
            fallback.setType("journal");
            fallback.setContent(rawText);
            fallback.setDetails("");
            return fallback;
        }
    }
}