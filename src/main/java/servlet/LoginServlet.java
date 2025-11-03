package servlet;

import jakarta.servlet.ServletException;
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

import exception.AppException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

 private UserController userController;

 @Override
 public void init() throws ServletException {
	 super.init();
	 UserRepository userRepository = new UserRepository();
     BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
     UserService userService = new UserService(userRepository, passwordEncoder);
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
         
         response.sendRedirect(request.getContextPath() + "/home");

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
