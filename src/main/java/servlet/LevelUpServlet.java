package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import repository.UserRepository;

@WebServlet("/api/level-notified")
public class LevelUpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userRepository = new UserRepository();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	String origen = request.getHeader("Referer");
        System.out.println("El Servlet LevelUp fue llamado desde: " + origen);
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("usuarioLogueado");
        try {
            int newLevel = Integer.parseInt(request.getParameter("level"));
            
            // Actualiza en BD
            userRepository.markLevelAsNotified(user.getId(), newLevel);
            
            // Actualiza en Sesión
            user.setNivelNotificado(newLevel);
            session.setAttribute("usuarioLogueado", user);
            
            response.getWriter().write("{\"success\": true}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}