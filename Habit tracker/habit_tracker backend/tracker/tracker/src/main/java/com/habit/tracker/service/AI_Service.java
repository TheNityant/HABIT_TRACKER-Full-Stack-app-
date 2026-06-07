package com.habit.tracker.service; // Verify this matches your package name!

import com.habit.tracker.model.Journal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

@Service
public class AI_Service {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🟢 1. The Main Vision & Text Method
    public Journal analyzeAndParseEntry(String userInput, String mediaUrl) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        String promptText = "You are an intelligent 'Second Brain' assistant. " +
            "The user has provided the following text: '" + userInput + "'. " +
            "If an image is attached, analyze the image context alongside the text. " +
            "1. If it's a command ('Write a note about X', 'Summarize this photo'), generate the requested content. " +
            "2. If it's a raw log ('Benched 225', 'Here is my meal'), format it nicely. " +
            "Respond ONLY with a valid JSON object containing exactly two keys: " +
            "'type' (either 'journal' or 'notebook') and 'content' (the final text).";

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ArrayNode contents = root.putArray("contents");
            ObjectNode contentObj = contents.addObject();
            ArrayNode parts = contentObj.putArray("parts");

            // Attach the text prompt
            parts.addObject().put("text", promptText);

            // Attach the image if it exists
            if (mediaUrl != null && !mediaUrl.isEmpty()) {
                String fullUrl = mediaUrl.startsWith("http") ? mediaUrl : "https://habit-tracker-backend-o9bs.onrender.com" + mediaUrl;
                
                try (InputStream is = new URL(fullUrl).openStream()) {
                    byte[] imageBytes = is.readAllBytes();
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    String mimeType = fullUrl.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";

                    ObjectNode inlineData = mapper.createObjectNode();
                    inlineData.put("mimeType", mimeType);
                    inlineData.put("data", base64Image);
                    parts.addObject().set("inlineData", inlineData);
                } catch (Exception e) {
                    System.out.println("⚠️ Could not fetch image for AI Vision: " + e.getMessage());
                }
            }

            // Send to Google
            String requestBody = mapper.writeValueAsString(root);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            return extractJournalFromJson(response.getBody()); 

        } catch (Exception e) {
            e.printStackTrace();
            Journal fallback = new Journal();
            fallback.setType("journal");
            fallback.setContent(userInput);
            return fallback;
        }
    }

    // 🟢 2. The JSON Extractor Helper Method
    private Journal extractJournalFromJson(String geminiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(geminiResponse);
            
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    String aiTextResponse = parts.get(0).path("text").asText();
                    
                    aiTextResponse = aiTextResponse.replaceAll("```json", "").replaceAll("```", "").trim();
                    JsonNode aiJsonNode = mapper.readTree(aiTextResponse);
                    
                    Journal parsedJournal = new Journal();
                    parsedJournal.setType(aiJsonNode.path("type").asText("journal")); 
                    parsedJournal.setContent(aiJsonNode.path("content").asText(aiTextResponse)); 
                    
                    return parsedJournal;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error parsing AI response: " + e.getMessage());
        }
        
        Journal fallback = new Journal();
        fallback.setType("journal");
        fallback.setContent("Media saved, but AI parsing failed.");
        return fallback;
    }
}