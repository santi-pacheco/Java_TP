package filter;

import java.io.IOException;
import java.io.PrintWriter; // Importar PrintWriter
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException; 
import exception.AppException;

@WebFilter("/*")
public class GlobalErrorHandlerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // Intenta continuar con la petición normal
            chain.doFilter(request, response);
            
        } catch (Exception ex) {
            // ERROR CAPTURADO
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String errorMessage = "Ha ocurrido un error inesperado.";
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            // Personalizamos el mensaje si es un error conocido
            if (ex instanceof AppException) {
                AppException appEx = (AppException) ex;
                errorMessage = appEx.getMessage();
                statusCode = appEx.getStatusCode();
                System.out.println("AppException capturada por el FILTRO: " + errorMessage);

            } else if (ex instanceof ConstraintViolationException) {
                errorMessage = "Datos inválidos (Violación de restricción)";
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                System.out.println("ConstraintViolationException capturada por el FILTRO: " + errorMessage);
            
            } else {
                System.out.println("Excepción no controlada capturada por el FILTRO: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            httpResponse.setStatus(statusCode);

            // Verificamos si la petición esperaba una respuesta JSON 
            String acceptHeader = httpRequest.getHeader("Accept");
            String requestedWithHeader = httpRequest.getHeader("X-Requested-With");
            
            boolean isJsonRequest = (acceptHeader != null && acceptHeader.contains("application/json")) || 
                                    ("XMLHttpRequest".equals(requestedWithHeader));

            if (isJsonRequest) {
                // RESPONDER CON JSON
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                
                // Creamos un JSON de error
                String jsonError = String.format(
                    "{\"success\":false, \"status\":%d, \"message\":\"%s\"}",
                    statusCode,
                    errorMessage.replace("\"", "\\\"")
                );
                PrintWriter out = httpResponse.getWriter();
                out.print(jsonError);
                out.flush();
            } else {
                // RESPONDER CON HTML
                httpRequest.setAttribute("errorMessage", errorMessage);
                httpRequest.setAttribute("statusCode", statusCode);
                
                RequestDispatcher dispatcher = httpRequest.getRequestDispatcher("/error.jsp");
                dispatcher.forward(httpRequest, response);
            }
        }
    }
}