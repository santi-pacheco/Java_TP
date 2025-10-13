package filter;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException; // <-- Importante
import exception.AppException;

@WebFilter("/*")
public class GlobalErrorHandlerFilter implements Filter {

    private static final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json;charset=UTF-8");

            // Errores personalizados de la aplicación (AppException)
            if (ex instanceof AppException) {
                AppException appEx = (AppException) ex;
                httpResponse.setStatus(appEx.getStatusCode());
                System.out.println("AppException capturada: " + appEx.getMessage() + " Tipo: " + appEx.getErrorType());

                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("success", false);
                payload.put("message", appEx.getMessage());
                payload.put("type", appEx.getErrorType());

                String jsonResponse;
                try {
                    jsonResponse = gson.toJson(payload);
                } catch (Exception serEx) {
                    System.out.println("Error serializing AppException payload");
                    jsonResponse = "{\"success\":false,\"message\":\"Error serializing response\"}";
                }

                try {
                    httpResponse.getWriter().write(jsonResponse);
                    httpResponse.getWriter().flush();
                } catch (IOException ioEx) {
                    System.out.println("No se pudo escribir la respuesta (AppException)");
                }
                return; // salir después de escribir la respuesta
            }
            // Manejo de errores de validación de Jakarta Bean Validation
            else if (ex instanceof ConstraintViolationException) {
                ConstraintViolationException constraintEx = (ConstraintViolationException) ex;
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("ConstraintViolationException: " + constraintEx.getMessage());

                List<Map<String, String>> formattedErrors = constraintEx.getConstraintViolations().stream()
                        .map(v -> {
                            Map<String, String> m = new LinkedHashMap<>();
                            m.put("field", v.getPropertyPath().toString());
                            m.put("message", v.getMessage());
                            return m;
                        })
                        .collect(Collectors.toList());

                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("success", false);
                payload.put("message", "Datos inválidos");
                payload.put("errors", formattedErrors);

                String jsonResponse;
                try {
                    jsonResponse = gson.toJson(payload);
                } catch (Exception serEx) {
                    System.out.println("Error serializing ConstraintViolation payload");
                    jsonResponse = "{\"success\":false,\"message\":\"Datos inválidos\"}";
                }

                try {
                    httpResponse.getWriter().write(jsonResponse);
                    httpResponse.getWriter().flush();
                } catch (IOException ioEx) {
                    System.out.println("No se pudo escribir la respuesta (ConstraintViolation)");
                }
                return;
            }
            // Errores no controlados (500)
            else {
                // Mensaje básico en consola y stacktrace para debugging
                System.out.println("Unhandled exception: " + ex.getMessage());
                ex.printStackTrace();

                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("success", false);
                payload.put("message", "Error interno del servidor");

                String jsonResponse;
                try {
                    jsonResponse = gson.toJson(payload);
                } catch (Exception serEx) {
                    System.out.println("Error serializing 500 payload");
                    jsonResponse = "{\"success\":false,\"message\":\"Error interno del servidor\"}";
                }

                try {
                    httpResponse.getWriter().write(jsonResponse);
                    httpResponse.getWriter().flush();
                } catch (IOException ioEx) {
                    System.out.println("No se pudo escribir la respuesta (500)");
                }
                return;
            }
        }
    }
}
