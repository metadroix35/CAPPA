/*package com.captureai.controller;

import com.captureai.model.Response;
import com.captureai.services.OpenAI;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.lang.String;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CaptionController {

    public OpenAI ai;

    @PostMapping("/caption")
    public ResponseEntity<String> caption(@RequestParam("image") MultipartFile file) {
        try {

            if (file.isEmpty())
                return ResponseEntity.badRequest().body("No file uploaded");

            byte[] imageBytes = file.getBytes();
            String responseText = ai.generateCaptions(imageBytes);

            Response resp = new Response();


            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> root = mapper.readValue(raw, Map.class);

// Case 1: output_text contains the JSON
            if (root.containsKey("output_text")) {
                return root.get("output_text").toString();
            }

// Case 2: nested in output[0].content[0].text
            try {
                var output = (List<?>) root.get("output");
                var first = (Map<?, ?>) output.get(0);
                var content = (List<?>) first.get("content");
                var text = (Map<?, ?>) content.get(0);
                return text.get("text").toString();
            } catch (Exception ignored) {}

// fallback: return raw
            return raw;


        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}*/
/*package com.captureai.controller;

import com.captureai.model.Response;
import com.captureai.services.OpenAI;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CaptionController {

    private final  OpenAI ai;

    @PostMapping("/caption")
    public ResponseEntity<?> caption(@RequestParam("image") MultipartFile file) {
        try {

            if (file.isEmpty())
                return ResponseEntity.badRequest().body("No file uploaded");

            byte[] imageBytes = file.getBytes();
            String responseText = ai.generateCaptions(imageBytes);

            Response resp = new Response();
            ObjectMapper mapper = new ObjectMapper();

            // Try to parse as JSON
            try {
                Map<String, Object> parsed = mapper.readValue(responseText, Map.class);
                resp.setCaptions(parsed);
            } catch (Exception ignored) {
                // If parsing fails, store raw text
                resp.setRaw(responseText);
            }

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}*/

package com.captureai.controller;

import com.captureai.model.Response;
import com.captureai.services.OpenAI;   // or GeminiService/GroqService if you switch
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CaptionController {

    private final OpenAI ai;   // injected service

    @PostMapping("/caption")
    public ResponseEntity<?> caption(@RequestParam("image") MultipartFile file) {
        try {

            if (file.isEmpty())
                return ResponseEntity.badRequest().body("No file uploaded");

            byte[] imageBytes = file.getBytes();
            String responseText = ai.generateCaptions(imageBytes);

            Response resp = new Response();
            ObjectMapper mapper = new ObjectMapper();

            try {
                // Parse full OpenAI response
                Map<String, Object> root = mapper.readValue(responseText, Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    // Clean ```json ... ``` wrapper
                    String cleaned = content
                            .replace("```json", "")
                            .replace("```", "")
                            .trim();

                    // Parse the inner JSON with captions
                    Map<String, Object> captionJson = mapper.readValue(cleaned, Map.class);
                    resp.setCaptions(captionJson);

                } else {
                    resp.setRaw(responseText);
                }

            } catch (Exception e) {
                // If extraction or parsing fails, return raw response
                resp.setRaw(responseText);
            }

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}

