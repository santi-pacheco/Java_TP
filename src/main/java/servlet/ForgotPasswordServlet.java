package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import controller.UserController;
import repository.FollowRepository;
import repository.UserRepository;
import service.EmailService;
import service.UserService;
import exception.AppException;
import repository.BlockRepository;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private UserController userController;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        FollowRepository followRepository = new FollowRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
        this.userController = new UserController(userService);
        this.emailService = (EmailService) getServletContext().getAttribute("emailService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/forgot-password?error=true");
            return;
        }
        try {
            String token = userController.generatePasswordResetToken(email);      
            if (token != null) {
                String htmlBody = leerPlantillaHtml("/WEB-INF/templates/reset-password.html");
                String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/reset-password?token=" + token;  
                htmlBody = htmlBody.replace("{{RESET_LINK}}", resetLink);
                emailService.sendEmail(email, "Recupera tu acceso a FatMovies 🍿", htmlBody);
            }
            response.sendRedirect(request.getContextPath() + "/forgot-password?success=true");
        } catch (AppException e) {
            System.err.println("Error controlado enviando email de recuperación: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/forgot-password?error=true");     
        } catch (Exception e) {
            throw new ServletException("Error crítico en la recuperación de contraseña", e);
        }
    }

    private String leerPlantillaHtml(String ruta) throws IOException {
        InputStream is = getServletContext().getResourceAsStream(ruta);
        if (is == null) {
            throw new IOException("No se encontró la plantilla en: " + ruta);
        }
        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }
}