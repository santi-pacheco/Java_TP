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
import exception.ErrorFactory;
import exception.AppException;
import repository.BlockRepository;
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
        BlockRepository blockRepository = new BlockRepository();
        this.userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
       
        HttpSession session = request.getSession(false);
        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        String action = request.getParameter("action");
        try {
            if ("set".equals(action)) {
                String movieIdStr = request.getParameter("movieId");
                if (movieIdStr == null || movieIdStr.isEmpty()) throw ErrorFactory.badRequest("ID de película requerido.");
                
                int movieId = Integer.parseInt(movieIdStr);
                userService.updatePlatoPrincipal(loggedUser.getUserId(), movieId);
                loggedUser.setMainDishMovieId(movieId);
                session.setAttribute("flashMessage", "¡Plato principal actualizado!");
                session.setAttribute("flashType", "success");
                
            } else if ("remove".equals(action)) {
                userService.updatePlatoPrincipal(loggedUser.getUserId(), null);
                loggedUser.setMainDishMovieId(null);
                session.setAttribute("flashMessage", "Plato principal removido.");
                session.setAttribute("flashType", "info");
            } else {
                throw ErrorFactory.badRequest("Acción no válida.");
            }
            session.setAttribute("usuarioLogueado", loggedUser);
            response.sendRedirect(request.getContextPath() + "/profile?id=" + loggedUser.getUserId());
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El ID de la película debe ser un número entero.");
        } catch (AppException e) {
            session.setAttribute("flashMessage", "⚠️ " + e.getMessage());
            session.setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/profile?id=" + loggedUser.getUserId());
        }
    }
}