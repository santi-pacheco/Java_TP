package servlet;

import java.io.File;
import repository.BlockRepository;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import controller.UserController;
import entity.User;
import exception.AppException;
import exception.ErrorFactory;
import repository.FollowRepository;
import repository.UserRepository;
import service.UserService;
import validations.OnCreate;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private UserController userController;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
        this.userController = new UserController(userService);
        
        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":
                List<User> usersList = userController.getUsers();
                request.setAttribute("users", usersList);
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/userCrud.jsp").forward(request, response);
                break;
                
            case "mostrarFormEditar":
                int idEditar = parseIntParam(request.getParameter("id"), "ID");
                User user = userController.getUserById(idEditar);
                if (user == null) throw ErrorFactory.notFound("Usuario no encontrado.");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/editarUsuario.jsp").forward(request, response);
                break;          
            case "mostrarFormCrear":
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/crearUsuario.jsp").forward(request, response);
                break;    
            default:
                throw ErrorFactory.badRequest("Acción desconocida");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            throw ErrorFactory.badRequest("Acción no especificada.");
        }
        if ("eliminar".equals(accion)) {
            int idEliminar = parseIntParam(request.getParameter("id"), "ID");
            User userToDelete = userController.getUserById(idEliminar);
            
            if (userToDelete != null) {
                if (userToDelete.getProfileImage() != null) {
                    String uploadPath = "C:" + File.separator + "fatmovies_uploads";
                    String fileName = java.nio.file.Paths.get(userToDelete.getProfileImage()).getFileName().toString();
                    File file = new File(uploadPath, fileName);
                    if (file.exists() && file.getCanonicalPath().startsWith(new File(uploadPath).getCanonicalPath())) {
                        file.delete();
                    }
                }
                userController.removeUser(userToDelete);
            }
            response.sendRedirect(request.getContextPath() + "/users?accion=listar&exito=true");
            return;
        }
        User userFromForm = new User();
        String jspTarget = "/WEB-INF/vistaUserCRUD/crearUsuario.jsp";
        try {
            if ("crear".equals(accion)) {
                jspTarget = "/WEB-INF/vistaUserCRUD/crearUsuario.jsp";
                
                userFromForm.setUsername(request.getParameter("username"));
                userFromForm.setEmail(request.getParameter("email"));
                userFromForm.setPassword(request.getParameter("password"));
                userFromForm.setRole(request.getParameter("role"));
                userFromForm.setBirthDate(parseDate(request.getParameter("birthDate")));
                
                Set<ConstraintViolation<User>> violations = validator.validate(userFromForm, Default.class, OnCreate.class);
                if (!violations.isEmpty()) {
                    request.setAttribute("errors", getErrorMessages(violations));
                    request.setAttribute("user", userFromForm);
                    request.getRequestDispatcher(jspTarget).forward(request, response);
                    return;
                }
                
                userController.createUser(userFromForm);  
            } else if ("actualizar".equals(accion)) {
                jspTarget = "/WEB-INF/vistaUserCRUD/editarUsuario.jsp";
                
                int id = parseIntParam(request.getParameter("id"), "ID");
                userFromForm.setUserId(id);
                userFromForm.setUsername(request.getParameter("username"));
                userFromForm.setEmail(request.getParameter("email"));
                userFromForm.setRole(request.getParameter("role"));
                userFromForm.setBirthDate(parseDate(request.getParameter("birthDate")));  
                String newPassword = request.getParameter("password");
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    userFromForm.setPassword(newPassword);
                } else {
                    userFromForm.setPassword(null);
                } 
                Set<ConstraintViolation<User>> violationsUpdate = validator.validate(userFromForm);
                if (!violationsUpdate.isEmpty()) {
                    request.setAttribute("errors", getErrorMessages(violationsUpdate));
                    request.setAttribute("user", userFromForm); 
                    request.getRequestDispatcher(jspTarget).forward(request, response);
                    return; 
                }
                userController.modifyUser(userFromForm); 
            } else {
                throw ErrorFactory.badRequest("Acción desconocida: " + accion);
            }       
            response.sendRedirect(request.getContextPath() + "/users?accion=listar&exito=true");
        } catch (AppException e) {
            if (e.getErrorType().equals("DUPLICATE_ERROR") || e.getErrorType().equals("VALIDATION_ERROR")) {
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
    
    private int parseIntParam(String param, String fieldName) {
        if (param == null || param.trim().isEmpty()) {
            throw ErrorFactory.validation("El campo '" + fieldName + "' no puede estar vacío.");
        }
        try {
            return Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            throw ErrorFactory.validation("El campo '" + fieldName + "' debe ser un número entero.");
        }
    }

    private Set<String> getErrorMessages(Set<ConstraintViolation<User>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}