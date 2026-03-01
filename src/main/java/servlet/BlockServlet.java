package servlet;

import java.io.IOException;
import repository.BlockRepository;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;
import entity.User;
import exception.ErrorFactory;

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
    	UserRepository userRepository = new UserRepository();
    	BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
    	FollowRepository followRepository = new FollowRepository();
    	BlockRepository blockRepository = new BlockRepository();
        this.userService = new UserService(userRepository, bcryptPasswordEncoder, followRepository, blockRepository);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
    	HttpSession session = request.getSession(false);
        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        
        String targetIdStr = request.getParameter("targetId");
        if (targetIdStr == null || targetIdStr.isEmpty()) {
            throw ErrorFactory.badRequest("El ID del usuario a bloquear es requerido");
        }

        int targetId = Integer.parseInt(targetIdStr);
        userService.toggleBlock(loggedUser.getUserId(), targetId);

        response.getWriter().write("{\"success\":true}");
    }
}