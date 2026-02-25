package servlet;

import java.io.IOException;
import repository.BlockRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import controller.UserController;
import service.UserService;
import repository.UserRepository;
import repository.FollowRepository;
import exception.AppException;

@WebServlet("/follow")
public class FollowServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        FollowRepository followRepository = new FollowRepository();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, encoder, followRepository, blockRepository);
        this.userController = new UserController(userService);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Detectar si la petición viene de nuestro AJAX (fetch)
        boolean isAjax = "true".equals(request.getParameter("ajax"));
        
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
        
        if (loggedUser == null) {
            if (isAjax) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false, \"error\":\"Debes iniciar sesión\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
            return;
        }
        
        String targetIdStr = request.getParameter("idUsuario");
        String redirectUrl = request.getContextPath() + "/home"; 
        
        try {
            if (targetIdStr == null || targetIdStr.isEmpty()) {
                throw exception.ErrorFactory.badRequest("ID de usuario no especificado.");
            }
            
            int targetId = Integer.parseInt(targetIdStr);
            redirectUrl = request.getContextPath() + "/profile?id=" + targetId;
            
            userController.handleFollowAction(loggedUser.getId(), targetId);

            // 2. Respuesta dependiendo de quién lo pidió
            if (isAjax) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":true}");
            } else {
                response.sendRedirect(redirectUrl);
            }

        } catch (AppException e) {
            if (isAjax) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false, \"error\":\"" + e.getMessage() + "\"}");
            } else {
                if (e.getStatusCode() == 500) {
                    throw new ServletException("Error crítico procesando follow", e);
                } else {
                    session.setAttribute("flashMessage", "⚠️ " + e.getMessage());
                    session.setAttribute("flashType", "warning");
                    response.sendRedirect(redirectUrl);
                }
            }
        } catch (Exception e) {
            if (isAjax) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false, \"error\":\"Error interno del servidor\"}");
            } else {
                throw new ServletException("Error crítico procesando follow", e);
            }
        }
    }
}