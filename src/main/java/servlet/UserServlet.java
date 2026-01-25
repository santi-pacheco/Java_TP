package servlet;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
// import com.google.gson.Gson; // <-- Ya no se necesita
import controller.UserController;
import entity.User;
import java.util.List;
import repository.UserRepository;
import service.UserService;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import exception.ErrorFactory;
// import com.google.gson.GsonBuilder; // <-- Ya no se necesita
import java.sql.Date; // Para convertir la fecha
import java.text.ParseException; // Para la fecha
import java.text.SimpleDateFormat; // Para la fecha
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import exception.AppException;
import validations.OnCreate;
import jakarta.validation.groups.Default;
import jakarta.servlet.ServletContext;
import repository.FollowRepository;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private UserController userController;
    private Validator validator;
    // private Gson gson; // <-- Ya no se necesita

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository);
        this.userController = new UserController(userService);
        
        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                List<User> usersList = userController.getUsers();
                request.setAttribute("users", usersList);
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/userCrud.jsp").forward(request, response);
                break;
                
            case "mostrarFormEditar":
            	int idEditar = 0;
            	try {
            		idEditar = Integer.parseInt(request.getParameter("id"));
            	} catch (NumberFormatException e) {
					throw ErrorFactory.badRequest("El ID proporcionado no es un número válido.");
				}
                User user = userController.getUserById(idEditar);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/editarUsuario.jsp").forward(request, response);
                break;
                
            case "mostrarFormCrear":
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/crearUsuario.jsp").forward(request, response);
                break;    
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");

        if (accion == null) {
            throw ErrorFactory.badRequest("No se especificó una acción en el formulario.");
        }
        
        User userFromForm = null; 
        String jspTarget = "";
        
        try {
            switch (accion) {
                case "crear":
                	jspTarget = "/WEB-INF/vistaUserCRUD/crearUsuario.jsp";
                	userFromForm = new User(); // Asigna a la variable externa
                	userFromForm.setUsername(request.getParameter("username"));
                	userFromForm.setEmail(request.getParameter("email"));
                	userFromForm.setPassword(request.getParameter("password"));
                	userFromForm.setRole(request.getParameter("role"));
                    Date birthDate = parseDate(request.getParameter("birthDate"));
                    userFromForm.setBirthDate(birthDate);
                    Set<ConstraintViolation<User>> violations = validator.validate(userFromForm, Default.class, OnCreate.class);
                    if (!violations.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violations));
                        request.setAttribute("user", userFromForm);
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return;
                    }
                    userController.createUser(userFromForm);
                    break;
                    
                case "actualizar":
                	jspTarget = "/WEB-INF/vistaUserCRUD/editarUsuario.jsp";
                	userFromForm = new User();
                    try {
                        int id = Integer.parseInt(request.getParameter("id"));
                        userFromForm.setId(id);
                    } catch (NumberFormatException e) {
                        throw ErrorFactory.badRequest("El ID proporcionado no es un número válido.");
                    }
                    userFromForm.setUsername(request.getParameter("username"));
                    userFromForm.setEmail(request.getParameter("email"));
                    userFromForm.setRole(request.getParameter("role"));
                    userFromForm.setBirthDate(parseDate(request.getParameter("birthDate")));

                    Set<ConstraintViolation<User>> violationsUpdate = validator.validate(userFromForm);
                    if (!violationsUpdate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsUpdate));
                        request.setAttribute("user", userFromForm); 
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return; 
                    }

                    userController.modifyUser(userFromForm);
                    break;
                    
                case "eliminar":
                	try {
	                    int idEliminar = Integer.parseInt(request.getParameter("id"));
	                    userFromForm = new User();
	                    userFromForm.setId(idEliminar); 
	                    userController.removeUser(userFromForm);
	                    
                   } catch (NumberFormatException e) {
						throw ErrorFactory.badRequest("El ID proporcionado no es un número válido.");
                   }
                   break;
            }

            response.sendRedirect(request.getContextPath() + "/users?accion=listar&exito=true");

        } catch (AppException e) {
            if (e.getErrorType().equals("DUPLICATE_ERROR")) {
                request.setAttribute("appError", e.getMessage());
                request.setAttribute("user", userFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);

            } else {
				//Error no corregible: Re-lanzar
            	throw e;
            }
        }
    }

    // --- Funciones de Ayuda ---

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