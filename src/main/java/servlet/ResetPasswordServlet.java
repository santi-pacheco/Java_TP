package servlet;

import jakarta.servlet.ServletException;
import repository.BlockRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import controller.UserController;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        FollowRepository followRepository = new FollowRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
        this.userController = new UserController(userService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");

        if (token != null && userController.validateResetToken(token)) {
            request.setAttribute("token", token);
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/forgot-password?error=invalid_token");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword"); 

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            return;
        }

        try {
            userController.resetPasswordWithToken(token, password);
            response.sendRedirect(request.getContextPath() + "/login?success=password_reset");

        } catch (Exception e) {
            request.setAttribute("error", "Error al cambiar la contraseña. El enlace pudo haber expirado.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
        }
    }
}