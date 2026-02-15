package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import validations.OnCreate;

import entity.User;
import exception.AppException;
import exception.ErrorFactory;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.validation.groups.Default;
import jakarta.servlet.ServletContext;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserService userService;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        this.userService = new UserService(userRepository, passwordEncoder, followRepository);
        
        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String jspTarget = "/register.jsp";
        User userFromForm = new User();
        
        try {
        	
            userFromForm.setUsername(request.getParameter("username"));
            userFromForm.setEmail(request.getParameter("email"));
            userFromForm.setPassword(request.getParameter("password"));
            userFromForm.setBirthDate(parseDate(request.getParameter("birthDate")));
            
            userFromForm.setRole("user"); 

            String confirmPassword = request.getParameter("confirmPassword");
            if (userFromForm.getPassword() == null || !userFromForm.getPassword().equals(confirmPassword)) {
                request.setAttribute("appError", "Las contraseñas no coinciden.");
                request.setAttribute("user", userFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
                return;
            }

            Set<ConstraintViolation<User>> violations = validator.validate(userFromForm, Default.class, OnCreate.class);
            
            if (!violations.isEmpty()) {
                request.setAttribute("errors", getErrorMessages(violations));
                request.setAttribute("user", userFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
                return;
            }

            User createdUser = userService.CreateUser(userFromForm);
            
            try {
                repository.WatchlistRepository watchlistRepo = new repository.WatchlistRepository(new repository.MovieRepository());
                watchlistRepo.addWatchlist(createdUser.getId());
            } catch (Exception e) {
                System.err.println("Error creating watchlist for new user: " + e.getMessage());
            }
            
            request.getSession().setAttribute("usuarioLogueado", createdUser);

            response.sendRedirect(request.getContextPath() + "/");

        } catch (AppException e) {
            if (e.getErrorType().equals("DUPLICATE_ERROR")) {
                request.setAttribute("appError", e.getMessage());
                request.setAttribute("user", userFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
            } else {
            	throw e;
            }
        }
    }

    private Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(dateString);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            throw ErrorFactory.validation("Formato de fecha inválido. Usar yyyy-MM-dd.");
        }
    }

    private Set<String> getErrorMessages(Set<ConstraintViolation<User>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}