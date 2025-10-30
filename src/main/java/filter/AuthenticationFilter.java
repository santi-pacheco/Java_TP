package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

//@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // 1. Convertimos los objetos request/response
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 2. Obtenemos la URI que el usuario está intentando visitar
        String requestURI = httpRequest.getRequestURI();
        
        // 3. Definimos las rutas "públicas" (que no requieren login)
        //    (Ajusta esto a tus necesidades, ej: /registro.jsp)
        boolean isLoginServlet = requestURI.endsWith("/login");
        boolean isRegisterServlet = requestURI.endsWith("/register");
        boolean isPublicResource = requestURI.startsWith(httpRequest.getContextPath() + "/css/") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/js/") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/images/");

        // 4. Verificamos si el usuario está logueado
        //    Pedimos la sesión con 'false' para NO crear una si no existe.
        HttpSession session = httpRequest.getSession(false); 

        boolean isLoggedIn = (session != null && session.getAttribute("usuarioLogueado") != null);

        // 5. Lógica de Decisión (El núcleo del filtro)
        
        if (isLoggedIn || isLoginServlet || isPublicResource || isRegisterServlet) {
            // Caso A: El usuario SÍ está logueado
            // O está intentando acceder a una página pública (como el propio login)
            System.out.println("AuthenticationFilter: Usuario logueado o ruta pública. Permitiendo acceso a " + requestURI);
            chain.doFilter(request, response);
            
        } else {
            // Caso B: El usuario NO está logueado Y está pidiendo una ruta privada
            
            // Lo redirigimos a la página de login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	
    }

    @Override
    public void destroy() {
        
    }
}