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
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import exception.ErrorFactory;
// import com.google.gson.GsonBuilder; // <-- Ya no se necesita
import java.sql.Date; // Para convertir la fecha
import java.text.ParseException; // Para la fecha
import java.text.SimpleDateFormat; // Para la fecha
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import exception.AppException;
import validations.OnCreate;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private UserController userController;
    private Validator validator; // El validador sigue siendo muy útil
    // private Gson gson; // <-- Ya no se necesita

    @Override
    public void init() throws ServletException {
        super.init();
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserService userService = new UserService(userRepository, passwordEncoder);
        this.userController = new UserController(userService);
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtenemos la acción. Si no viene, la acción por defecto es "listar".
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        // 2. Usamos un switch para decidir qué página JSP mostrar
        switch (accion) {
            case "listar":
                // Obtenemos la lista
                List<User> usersList = userController.getUsers();
                // La "pegamos" en el request
                request.setAttribute("users", usersList);
                // Reenviamos al JSP de la lista
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/userCrud.jsp").forward(request, response);
                break;
                
            case "mostrarFormEditar":
                // Obtenemos el ID del usuario a editar
            	int idEditar = 0;
            	try {
            		idEditar = Integer.parseInt(request.getParameter("id"));
            	} catch (NumberFormatException e) {
					throw ErrorFactory.badRequest("El ID proporcionado no es un número válido.");
				}
                User user = userController.getUserById(idEditar);
                // Lo "pegamos" en el request
                request.setAttribute("user", user);
                // Reenviamos al JSP del formulario de edición
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/editarUsuario.jsp").forward(request, response);
                break;
                
            case "mostrarFormCrear":
                // Solo mostramos el formulario de creación
                request.getRequestDispatcher("/WEB-INF/vistaUserCRUD/crearUsuario.jsp").forward(request, response);
                break;    
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtenemos la acción. Es obligatorio en un POST.
        String accion = request.getParameter("accion");

        if (accion == null) {
            throw ErrorFactory.badRequest("No se especificó una acción en el formulario.");
        }
        
        User userFromForm = null; 
        String jspTarget = ""; // También para el JSP target
        
        try {
            switch (accion) {
                case "crear":
                	jspTarget = "/WEB-INF/vistaUserCRUD/crearUsuario.jsp";
                	userFromForm = new User(); // Asigna a la variable externa
                    // 2. VINCULAR: Creamos el objeto User desde los parámetros del formulario
                	userFromForm.setUsername(request.getParameter("username"));
                	userFromForm.setEmail(request.getParameter("email"));
                	userFromForm.setPassword(request.getParameter("password"));
                	userFromForm.setRole(request.getParameter("role"));
                    // Las fechas requieren conversión
                    Date birthDate = parseDate(request.getParameter("birthDate"));
                    userFromForm.setBirthDate(birthDate);

                    // 3. VALIDAR: (¡Igual que antes!)
                    Set<ConstraintViolation<User>> violations = validator.validate(userFromForm, OnCreate.class);
                    if (!violations.isEmpty()) {
                        // Manejar error de validación (quizás reenviar al formulario con mensajes)
                        request.setAttribute("errors", getErrorMessages(violations));
                        request.setAttribute("user", userFromForm); // Devolver datos para "repoblar" el form
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return; // Importante salir para no redirigir
                    }

                    // 4. ACTUAR:
                    userController.createUser(userFromForm);
                    break;
                    
                case "actualizar":
                    // 2. VINCULAR:
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

                    // 3. VALIDAR:
                    Set<ConstraintViolation<User>> violationsUpdate = validator.validate(userFromForm);
                    if (!violationsUpdate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsUpdate));
                        request.setAttribute("user", userFromForm); 
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return; 
                    }
                    
                    // 4. ACTUAR:
                    userController.modifyUser(userFromForm);
                    break;
                    
                case "eliminar":
                	try {
	                    // 2. VINCULAR (solo el ID):
	                    int idEliminar = Integer.parseInt(request.getParameter("id"));
	                    userFromForm = new User();
	                    userFromForm.setId(idEliminar); 
	                    // 4. ACTUAR:
	                    userController.removeUser(userFromForm);
	                    
                   } catch (NumberFormatException e) {
						throw ErrorFactory.badRequest("El ID proporcionado no es un número válido.");
                   }
                   break;
            }

            // 5. REDIRIGIR: Patrón Post-Redirect-Get (PRG)
            // Después de CUALQUIER acción POST exitosa, redirige al listado.
            // Esto evita que el formulario se reenvíe si el usuario actualiza la página.
            response.sendRedirect(request.getContextPath() + "/users?accion=listar&exito=true");

        } catch (AppException e) {
            if (e.getErrorType().equals("DUPLICATE_ERROR")) {
                // ✅ Error corregible: Devolver al formulario
                request.setAttribute("appError", e.getMessage());
                request.setAttribute("user", userFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);

            } else {
				// ❌ Error no corregible: Re-lanzar
            	throw e;
            }
        }
    }

    // --- Métodos `doPut`, `doPatch`, `doDelete` se eliminan ---
    

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