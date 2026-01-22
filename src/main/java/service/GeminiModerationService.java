package service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class GeminiModerationService {
    private static final String API_KEY;
    private static final String API_URL;

    static {
        ResourceBundle config = ResourceBundle.getBundle("config");
        API_KEY = config.getString("gemini.api.key").trim();
        API_URL = config.getString("gemini.api.url").trim();
    }


    public static class ModerationResult {
        private final boolean hasSpoilers;
        private final boolean hasOffensiveContent;
        private final String reason;

        public ModerationResult(boolean hasSpoilers, boolean hasOffensiveContent, String reason) {
            this.hasSpoilers = hasSpoilers;
            this.hasOffensiveContent = hasOffensiveContent;
            this.reason = reason;
        }

        public boolean hasSpoilers() {
            return hasSpoilers;
        }

        public boolean hasOffensiveContent() {
            return hasOffensiveContent;
        }

        public String getReason() {
            return reason;
        }
    }

    public ModerationResult moderateReview(String reviewText, String moviePlot, String movieTitle) {
        try {
            String prompt = buildPrompt(reviewText, moviePlot, movieTitle);
            String response = callGeminiAPI(prompt);
            return parseResponse(response);
        } catch (Exception e) {
            System.err.println("Error al moderar reseña: " + e.getMessage());
            e.printStackTrace();
            return new ModerationResult(false, false, "Error en la moderación: " + e.getMessage());
        }
    }

    private String buildPrompt(String reviewText, String moviePlot, String movieTitle) {
        return String.format(
            "Eres un sistema de moderación de reseñas de películas. Tu tarea es analizar la siguiente reseña usando el método del 'Tribunal de Expertos':\n\n" +
            "**Título de la Película:** %s\n\n" +
            "**Contexto de la Película:**\n%s\n\n" +
            "**Reseña a Analizar:**\n%s\n\n" +
            "**REGLA DE EXCEPCIÓN (VAGUEDAD PERMITIDA):**\n" +
            "Frases genéricas como 'el final es impactante', 'tiene un gran giro', 'muy triste' o 'no me lo esperaba' ESTÁN PERMITIDAS. Generan expectativa pero NO son spoilers. Solo marca SPOILER si la reseña explica QUÉ sucede específicamente (ej: 'impactante porque muere X').\n\n" +
            "Evalúa la reseña desde tres perspectivas:\n" +
            "1. **Juez Literal**: ¿El texto explícitamente revela detalles importantes de la trama, giros argumentales o el final?\n" +
            "2. **Juez de Intención**: ¿Hay sarcasmo, ironía o 'guiños' que impliquen spoilers indirectos?\n" +
            "3. **Juez de Hechos**: Comparando con el argumento oficial, ¿la reseña revela información que arruinaría la experiencia?\n\n" +
            "Además, verifica si contiene:\n" +
            "- Lenguaje ofensivo, insultos, discriminación o contenido inapropiado.\n\n" +
            "Responde SOLO en formato JSON (sin bloques de código):\n" +
            "{\n" +
            "  \"hasSpoilers\": true/false,\n" +
            "  \"hasOffensiveContent\": true/false,\n" +
            "  \"reason\": \"Explicación del veredicto\"\n" +
            "}",
            movieTitle,
            moviePlot,
            reviewText
        );
    }

    private String callGeminiAPI(String prompt) throws Exception {
        URL url = new URL(API_URL + "?key=" + API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        JSONObject requestBody = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();

        part.put("text", prompt);
        parts.put(part);
        content.put("parts", parts);
        contents.put(content);
        requestBody.put("contents", contents);

        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0);
        requestBody.put("generationConfig", generationConfig);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                throw new RuntimeException("Error en API Gemini: HTTP " + responseCode + 
                                         " - " + errorResponse.toString());
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        }
    }

    private ModerationResult parseResponse(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray candidates = response.getJSONArray("candidates");
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject content = candidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            String text = parts.getJSONObject(0).getString("text");

            if (text == null || text.trim().isEmpty()) {
                return new ModerationResult(false, false, "Error: Respuesta vacía de la IA");
            }

            int startIdx = text.indexOf("{");
            int endIdx = text.lastIndexOf("}");

            if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {

                String jsonText = text.substring(startIdx, endIdx + 1);
                JSONObject result = new JSONObject(jsonText);
                

                return new ModerationResult(
                    result.optBoolean("hasSpoilers", false),
                    result.optBoolean("hasOffensiveContent", false),
                    result.optString("reason", "Sin razón proporcionada")
                );
            } else {
          
                return new ModerationResult(false, false, "Error: Formato de respuesta inválido (No JSON)");
            }
            
        } catch (Exception e) {
            System.err.println("Error al parsear respuesta de Gemini: " + e.getMessage());
            return new ModerationResult(false, false, "Error técnico al procesar la respuesta");
        }
    }
}