package servlet;

import java.io.IOException;
import repository.BlockRepository;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;
import entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/block")
public class BlockServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService(new UserRepository(), new BCryptPasswordEncoder(), new FollowRepository(), new BlockRepository());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false, \"message\":\"No autenticado\"}");
            return;
        }

        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        String targetIdStr = request.getParameter("targetId");

        if (targetIdStr == null || targetIdStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false, \"message\":\"ID requerido\"}");
            return;
        }

        try {
            int targetId = Integer.parseInt(targetIdStr);
            userService.toggleBlock(loggedUser.getId(), targetId);
            response.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false, \"message\":\"Error interno\"}");
        }
    }
}