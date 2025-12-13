/*package com.captureai.services;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAI {

    @Value("${openai.api.key}")
    private String apiKey;

    public String generateCaptions(byte[] imageBytes) {
        try {
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String dataUri = "data:image/jpeg;base64," + base64;

            String body = """
                    {
                      "model": "gpt-4o-mini-vision",
                      "input": [
                        { "role": "user", "content": "Return ONLY compact JSON: short, funny, poetic, travel_tip." },
                        { "role": "user", "content": { "type": "input_image", "image_url": "%s" } }
                      ]
                    }
                    """.formatted(dataUri);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://api.openai.com/v1/responses");
            post.addHeader("Authorization", "Bearer " + apiKey);
            post.addHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(body));

            var response = client.execute(post);
            String raw = new String(response.getEntity().getContent().readAllBytes());
            System.out.println("\n\nRAW RESPONSE >>> " + raw + "\n\n");

            // Extract actual output text
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> root = mapper.readValue(raw, Map.class);

            // If OpenAI returned output_text field
            if (root.containsKey("output_text")) {
                return root.get("output_text").toString();
            }

            // Try nested output → content → text
            try {
                var output = (List<?>) root.get("output");
                var first = (Map<?, ?>) output.get(0);
                var content = (List<?>) first.get("content");
                var text = (Map<?, ?>) content.get(0);
                return text.get("text").toString();
            } catch (Exception ignored) {
            }

            // fallback: return the entire body
            return raw;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}*/





/*public String generateCaptions(byte[] imageBytes) {
        try {

            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String dataUri = "data:image/jpeg;base64," + base64;

            String body = """
            {
              "model": "gpt-4o-mini-vision",
              "input": [
                { "role": "user", "content": "Provide JSON captions: short, funny, poetic, travel_tip." },
                { "role": "user", "content": { "type": "input_image", "image_url": "%s" } }
              ]
            }
            """.formatted(dataUri);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://api.openai.com/v1/responses");
            post.addHeader("Authorization", "Bearer " + apiKey);
            post.addHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(body));

            var response = client.execute(post);
            return new String(response.getEntity().getContent().readAllBytes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
*/

/*package com.captureai.services;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class OpenAI {

    @Value("${openai.api.key}")
    private String apiKey;

    public String generateCaptions(byte[] imageBytes) {

        try {
            // Encode image
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String dataUri = "data:image/jpeg;base64," + base64;

            // OpenAI request body
            String body = """
            {
              "model": "gpt-4o-mini-vision",
              "input": [
                { "role": "user", "content": "Give captions as compact JSON: short, funny, poetic, travel_tip" },
                { "role": "user", "content": { "type": "input_image", "image_url": "%s" } }
              ]
            }
            """.formatted(dataUri);

            // Build request
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://api.openai.com/v1/responses");
            post.addHeader("Authorization", "Bearer " + apiKey);
            post.addHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(body));

            // Execute
            var response = client.execute(post);
            String raw = new String(response.getEntity().getContent().readAllBytes());

            // PRINT RAW JSON FOR DEBUGGING (IMPORTANT)
            System.out.println("\n\nRAW RESPONSE >>> " + raw + "\n\n");

            // Return raw to controller
            return raw;

        } catch (Exception e) {
            throw new RuntimeException("OpenAI request failed: " + e.getMessage());
        }
    }
}*/
package com.captureai.services;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.net.http.*;
import java.net.URI;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

@Service
public class OpenAI {

    @Value("${openai.api.key}")
    private String apiKey;

    public String generateCaptions(byte[] imageBytes) throws Exception {

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Build the content array using the NEW correct types
        JSONArray content = new JSONArray()
                .put(new JSONObject()
                        .put("type", "text")
                        .put("text", "Describe this travel image. Respond ONLY in compact JSON with keys: short, funny, poetic, travel_tip.")
                )
                .put(new JSONObject()
                        .put("type", "image_url")
                        .put("image_url", new JSONObject()
                                .put("url", "data:image/jpeg;base64," + base64Image)
                        )
                );

        // Build messages array
        JSONArray messages = new JSONArray()
                .put(new JSONObject()
                        .put("role", "user")
                        .put("content", content)
                );

        JSONObject body = new JSONObject()
                .put("model", "gpt-4o-mini")   // or gpt-4o
                .put("messages", messages);
               // .put("max_output_tokens", 150);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\nRAW RESPONSE >>> " + response.body());

        return response.body();
    }
}

