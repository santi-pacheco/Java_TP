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

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        
        boolean isLoginServlet = requestURI.endsWith("/login");
        boolean isRegisterServlet = requestURI.endsWith("/register");
        boolean isLandingServlet = requestURI.endsWith("/landing");
        boolean isForgotPasswordServlet = requestURI.endsWith("/forgot-password");
        boolean isResetPasswordServlet = requestURI.endsWith("/reset-password");
        boolean isPublicResource = requestURI.startsWith(httpRequest.getContextPath() + "/css/") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/js/") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/images/") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/utils/") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/uploads/") ||
                                   requestURI.endsWith("/landing.jsp");

        HttpSession session = httpRequest.getSession(false);
        entity.User user = (session != null) ? (entity.User) session.getAttribute("usuarioLogueado") : null;
        boolean isLoggedIn = (user != null);
        boolean isAdmin = (user != null && "admin".equals(user.getRole()));
        
        boolean isAdminRoute = requestURI.endsWith("/admin.jsp") ||
                              requestURI.endsWith("/index.jsp") ||
                              requestURI.endsWith("/index") ||
                              requestURI.endsWith("/admin") ||
                              requestURI.equals(httpRequest.getContextPath() + "/") ||
                              requestURI.contains("/genres") ||
                              requestURI.contains("/countries") ||
                              (requestURI.contains("/movies") && !requestURI.contains("/movies-page")) ||
                              requestURI.contains("/users") ||
                              requestURI.contains("/reviews-admin") ||
                              requestURI.contains("/configuracion-reglas") ||
                              requestURI.contains("/admin/data-load");;
        
        if (isLoginServlet || isRegisterServlet || isLandingServlet || isForgotPasswordServlet || isResetPasswordServlet || isPublicResource) {
            chain.doFilter(request, response);
        } else if (isAdminRoute) {
            if (isAdmin) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            }
        } else if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
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