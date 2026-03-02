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
import exception.ErrorFactory;

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
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("usuarioLogueado");
        
        try {
            String levelParam = request.getParameter("level");
            if (levelParam == null || levelParam.isEmpty()) {
                throw ErrorFactory.badRequest("El nivel es requerido.");
            }
            int newLevel = Integer.parseInt(levelParam);
            userRepository.markLevelAsNotified(user.getUserId(), newLevel);
            user.setNotifiedLevel(newLevel);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true}");
            
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El nivel debe ser un número válido.");
        }
    }
}