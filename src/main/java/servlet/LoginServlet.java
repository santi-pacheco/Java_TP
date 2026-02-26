package servlet;

import jakarta.servlet.ServletException;
import repository.BlockRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import entity.User;
import controller.UserController;
import service.UserService;
import repository.UserRepository;
import repository.FollowRepository;
import exception.AppException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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
     
     request.getRequestDispatcher("/login.jsp").forward(request, response);
 }
 
 @Override
 protected void doPost(HttpServletRequest request, HttpServletResponse response) 
         throws ServletException, IOException {
     
     String username = request.getParameter("username");
     String password = request.getParameter("password");

     try {
         User usuarioValidado = userController.login(username, password);
         
         HttpSession session = request.getSession(true); 
         session.setAttribute("usuarioLogueado", usuarioValidado);
         
         if ("admin".equals(usuarioValidado.getRole())) {
        	 response.sendRedirect(request.getContextPath() + "/home");;
         } else {
             response.sendRedirect(request.getContextPath() + "/home");
         }

     } catch (AppException e) {
         if (e.getStatusCode() >= 400 && e.getStatusCode() < 500) {
             request.setAttribute("error", e.getMessage());
             request.getRequestDispatcher("login.jsp").forward(request, response); 
         } else {
             throw e; 
         }
     }
 }
}
