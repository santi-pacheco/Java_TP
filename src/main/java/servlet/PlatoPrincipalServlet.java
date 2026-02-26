package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import service.UserService;
import repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import repository.FollowRepository;

@WebServlet("/plato-principal")
public class PlatoPrincipalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        this.userService = new UserService(userRepository, passwordEncoder, followRepository);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        String action = request.getParameter("action");

        try {
            if ("set".equals(action)) {
                int movieId = Integer.parseInt(request.getParameter("movieId"));
                userService.updatePlatoPrincipal(loggedUser.getId(), movieId);
                loggedUser.setPlatoPrincipalMovieId(movieId);
            } else if ("remove".equals(action)) {
                userService.updatePlatoPrincipal(loggedUser.getId(), null);
                loggedUser.setPlatoPrincipalMovieId(null);
            }
            
            session.setAttribute("usuarioLogueado", loggedUser);
            response.sendRedirect(request.getContextPath() + "/profile");
            
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/profile?error=true");
        }
    }
}