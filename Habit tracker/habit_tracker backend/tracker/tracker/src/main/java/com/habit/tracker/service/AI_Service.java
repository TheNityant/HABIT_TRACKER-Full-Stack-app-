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
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        // 1. We tell Gemini exactly how to format the data
        String systemPrompt = "You are an AI for a fitness and habit tracker. Analyze this user text: '" + rawText + "'.\n" +
                "Classify it strictly as either a 'journal' (thoughts, day summary) or a 'metric' (workout, weight, specific reps/sets).\n" +
                "Respond ONLY with a raw JSON object (no markdown, no backticks). Format:\n" +
                "{\n" +
                "  \"type\": \"journal or metric\",\n" +
                "  \"content\": \"For journal, polish the text slightly. For metric, just the exercise/metric name (e.g., 'Bench Press').\",\n" +
                "  \"details\": \"For journal, leave empty. For metric, list the sets/reps/weight (e.g., '180lbs x 5, 5, 5').\"\n" +
                "}";

        // 2. Build the exact JSON structure Google's API expects
        Map<String, Object> part = new HashMap<>();
        part.put("text", systemPrompt);
        
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