package servlet;

import java.io.IOException;

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
        
        UserService userService = new UserService(userRepository, encoder, followRepository);
        this.userController = new UserController(userService);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
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
            response.sendRedirect(redirectUrl);

        } catch (AppException e) {
        	if (e.getStatusCode() == 500) {
        		throw new ServletException("Error crítico procesando follow", e);
			} else {
	            session.setAttribute("flashMessage", "⚠️ " + e.getMessage());
	            session.setAttribute("flashType", "warning");
	            response.sendRedirect(redirectUrl);
			}
        } catch (Exception e) {
            throw new ServletException("Error crítico procesando follow", e);
        }
    }
}