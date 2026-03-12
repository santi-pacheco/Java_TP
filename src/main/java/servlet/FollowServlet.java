package servlet;

import java.io.IOException;
import repository.BlockRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import entity.User;
import exception.ErrorFactory;
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
        
        boolean isAjax = "true".equals(request.getParameter("ajax"));
        HttpSession session = request.getSession(false);
        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        String targetIdStr = request.getParameter("idUsuario");
        int targetId;

        try {
            if (targetIdStr == null || targetIdStr.isEmpty()) {
                throw ErrorFactory.badRequest("ID de usuario no especificado.");
            }
            targetId = Integer.parseInt(targetIdStr);
            userController.handleFollowAction(loggedUser.getUserId(), targetId);
            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":true}");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile?id=" + targetId);
            }
        } catch (AppException e) {
            if (isAjax) {
                throw e;
            } else {
                targetId = (targetIdStr != null && !targetIdStr.isEmpty()) ? Integer.parseInt(targetIdStr) : loggedUser.getUserId();
                session.setAttribute("flashMessage", e.getMessage());
                session.setAttribute("flashType", "warning");
                response.sendRedirect(request.getContextPath() + "/profile?id=" + targetId);
            }
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El ID de usuario es inválido.");
        }
    }
}