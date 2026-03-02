package servlet;

import java.io.IOException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import repository.BlockRepository;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;
import exception.ErrorFactory;

@WebServlet("/api/level-notified")
public class LevelUpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        FollowRepository followRepository = new FollowRepository();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, encoder, followRepository, blockRepository);
        this.userService = userService;
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
            userService.markLevelAsNotified(user.getUserId(), newLevel);
            user.setNotifiedLevel(newLevel);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true}");
            
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El nivel debe ser un número válido.");
        }
    }
}