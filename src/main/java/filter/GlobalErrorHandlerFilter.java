package filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher; // ¡Importante!
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest; // ¡Importante!
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException; 
import exception.AppException;

@WebFilter("/*")
public class GlobalErrorHandlerFilter implements Filter {

    // private static final Gson gson = new Gson(); // <-- Ya no se necesita

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // 1. Intenta continuar con la petición normal
            chain.doFilter(request, response);
            
        } catch (Exception ex) {
            // 2. ¡ERROR CAPTURADO! (Uno que no fue capturado por el servlet)
            
            // 3. Preparamos la respuesta
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String errorMessage = "Ha ocurrido un error inesperado.";
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            // 4. Personalizamos el mensaje si es un error conocido
            if (ex instanceof AppException) {
                AppException appEx = (AppException) ex;
                errorMessage = appEx.getMessage();
                statusCode = appEx.getStatusCode();
                System.out.println("AppException capturada por el FILTRO: " + errorMessage);

            } else if (ex instanceof ConstraintViolationException) {
                // Esto es una red de seguridad. El servlet ya debería haber
                // capturado esto. Si llega aquí, es un error 400.
                errorMessage = "Datos inválidos (Violación de restricción)";
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                System.out.println("ConstraintViolationException capturada por el FILTRO: " + errorMessage);
            
            } else {
                // 5. Errores genéricos (NullPointerException, SQLException, etc.)
                System.out.println("Excepción no controlada capturada por el FILTRO: " + ex.getMessage());
                ex.printStackTrace(); // ¡Importante para depurar!
            }
            
            // 6. Ponemos el error en el request para que el JSP lo lea
            httpRequest.setAttribute("errorMessage", errorMessage);
            httpRequest.setAttribute("statusCode", statusCode);
            
            // 7. Establecemos el código de estado (404, 500, etc.)
            httpResponse.setStatus(statusCode);
            
            // 8. Reenviamos a la página de error genérica
            RequestDispatcher dispatcher = httpRequest.getRequestDispatcher("/error.jsp");
            dispatcher.forward(httpRequest, response);
        }
    }
}