package servlet;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
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
import com.google.gson.GsonBuilder;


@WebServlet("/users")
public class UserServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private UserController userController;
	private Validator validator; // El validador que se usa en doPost y doPut
	private Gson gson = new Gson(); // Reutilizable para convertir objetos a JSON	
	
	@Override
	public void init() throws ServletException {
	    super.init();
	    
	    UserRepository userRepository = new UserRepository();
	    UserService userService = new UserService(userRepository);
	    
	    // 2. "Inyectamos" las dependencias en el constructor
	    this.userController = new UserController(userService);
	    
	    // 3. Inicializamos el validador una única vez
	    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    this.validator = factory.getValidator();
	    this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    if (request.getMethod().equalsIgnoreCase("PATCH")) {
	        this.doPatch(request, response);
	    } else {
	        super.service(request, response);
	    }
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    // 1. Verificamos si la petición es para una página web o para la API (JSON)
	    String acceptHeader = request.getHeader("Accept");
	    boolean isApiRequest = (acceptHeader != null && acceptHeader.contains("application/json"));
	    
	    if (isApiRequest) {
	        // --- MODO API (devuelve JSON) ---
	        response.setContentType("application/json;charset=UTF-8");
	        String idParam = request.getParameter("id");

	        if (idParam != null) {
	            int id = Integer.parseInt(idParam);
	            User user = userController.getUserById(id);
	            response.getWriter().write(this.gson.toJson(user));
	        } else {
	            List<User> users = userController.getUsers();
	            response.getWriter().write(this.gson.toJson(users));
	        }
	        
	    } else {
	        // --- MODO WEB (reenvía a JSP) ---
	        // Obtenemos la lista de usuarios. Si algo falla, el GlobalErrorHandlerFilter actuará.
	        List<User> usersList = userController.getUsers();
	        
	        // "Pegamos" la lista de usuarios en el objeto request para que el JSP pueda leerla.
	        request.setAttribute("users", usersList);
	        
	        // Le pasamos el control y los datos al archivo JSP para que genere el HTML.
	        request.getRequestDispatcher("/userCrud.jsp").forward(request, response);
	    }
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    // 1. VINCULAR: Creamos el objeto User con los datos de la petición.
		//Mostrar toda la info del request
		System.out.println("Request Method: " + request.getMethod());
		System.out.println("Request URL: " + request.getRequestURL().toString());
		System.out.println("Request Headers: ");
		request.getHeaderNames().asIterator()
		    .forEachRemaining(headerName -> 
		        System.out.println(headerName + ": " + request.getHeader(headerName))
		    );
	    User newUser = this.gson.fromJson(request.getReader(), User.class);
	    System.out.println("Objeto User creado desde JSON: " + this.gson.toJson(newUser));
		    // 2. VALIDAR: Usamos el validador para comprobar el objeto contra las anotaciones.
		    Set<ConstraintViolation<User>> violations = validator.validate(newUser);
		    if (!violations.isEmpty()) {
		        // Si hay errores, los juntamos en un solo mensaje y lanzamos una excepción de validación.
		        String errorMessages = violations.stream()
		                .map(ConstraintViolation::getMessage)
		                .collect(Collectors.joining(" "));
		        throw ErrorFactory.validation(errorMessages); // El Filter lo convertirá en un 400 Bad Request
		    }
	
		    // 3. ACTUAR: Llamamos al controller. El método ahora devuelve el usuario creado.
		    User createdUser = userController.createUser(newUser);
	
		    // Si llegamos aquí, todo salió bien. Enviamos la respuesta de éxito.
		    response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
		    response.setContentType("application/json;charset=UTF-8");
		    response.getWriter().write(new Gson().toJson(createdUser));
	    }

		@Override
		protected void doPut(HttpServletRequest request, HttpServletResponse response) 
		        throws ServletException, IOException {

		    // 1. OBTENER ID: Esto sigue igual, desde la URL. Es correcto.
		    String idParam = request.getParameter("id");
		    if (idParam == null || idParam.isEmpty()) {
		        throw ErrorFactory.badRequest("El ID del usuario es requerido para actualizar.");
		    }
		    int id = Integer.parseInt(idParam);

		    // 2. VINCULAR DESDE JSON: Leemos el cuerpo de la petición y lo convertimos con Gson.
		    // Esta es la sección modificada.
		    User userToUpdate = this.gson.fromJson(request.getReader(), User.class);

		    // ¡Importante! Asignamos el ID de la URL al objeto que creamos desde el JSON.
		    userToUpdate.setId(id);
	
		    // 3. VALIDAR: La validación funciona exactamente igual que antes.
		    Set<ConstraintViolation<User>> violations = validator.validate(userToUpdate);
		    if (!violations.isEmpty()) {
		        String errorMessages = violations.stream()
		                .map(ConstraintViolation::getMessage)
		                .collect(Collectors.joining(" "));
		        throw ErrorFactory.validation(errorMessages);
		    }
		    
		    // 4. ACTUAR: Llamamos al controller para modificar.
		    User updatedUser = userController.modifyUser(userToUpdate);
		    
		    // Enviamos la respuesta de éxito con el usuario actualizado.
		    response.setStatus(HttpServletResponse.SC_OK); // 200 OK
		    response.setContentType("application/json;charset=UTF-8");
		    response.getWriter().write(new Gson().toJson(updatedUser));
		}
	
		protected void doPatch(HttpServletRequest request, HttpServletResponse response) 
		        throws ServletException, IOException {

		    // 1. OBTENER ID: Igual que en PUT/DELETE
		    String idParam = request.getParameter("id");
		    if (idParam == null || idParam.isEmpty()) {
		        throw ErrorFactory.badRequest("El ID del usuario es requerido para actualizar.");
		    }
		    int id = Integer.parseInt(idParam);

		    // 2. LEER: Obtener el estado ACTUAL del usuario desde la base de datos.
		    // ¡Este paso es crucial y la principal diferencia con PUT!
		    User currentUser = userController.getUserById(id);
		    if (currentUser == null) {
		        throw ErrorFactory.notFound("Usuario no encontrado con ID: " + id);
		    }

		    // 3. VINCULAR CAMBIOS: Leer el JSON entrante a una estructura flexible como un Mapa.
		    // Esto nos permite ver qué campos envió el cliente para modificar.
		    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.Map<String, Object>>(){}.getType();
		    java.util.Map<String, Object> changes = this.gson.fromJson(request.getReader(), type);

		    // 4. MODIFICAR: Aplicar los cambios del mapa al objeto que trajimos de la BD.
		    // Verificamos la existencia de cada clave antes de intentar actualizar el campo.
		    if (changes.containsKey("username")) {
		        currentUser.setUsername((String) changes.get("username"));
		    }
		    if (changes.containsKey("email")) {
		        currentUser.setEmail((String) changes.get("email"));
		    }
		    if (changes.containsKey("password")) {
		        // En un caso real, la contraseña debería ser hasheada aquí
		        currentUser.setPassword((String) changes.get("password"));
		    }
		    if (changes.containsKey("role")) {
		        currentUser.setRole((String) changes.get("role"));
		    }
		    if (changes.containsKey("birthDate")) {
		        // Necesitamos convertir el String de la fecha de vuelta a un objeto Date
		        try {
		            java.util.Date birthDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse((String) changes.get("birthDate"));
		            currentUser.setBirthDate(new java.sql.Date(birthDate.getTime()));
		        } catch (java.text.ParseException e) {
		            throw ErrorFactory.badRequest("Formato de fecha inválido. Usar yyyy-MM-dd.");
		        }
		    }
		    
		    // 5. VALIDAR: Validamos el objeto COMPLETO después de aplicar los cambios.
		    Set<ConstraintViolation<User>> violations = validator.validate(currentUser);
		    if (!violations.isEmpty()) {
		        String errorMessages = violations.stream()
		                .map(ConstraintViolation::getMessage)
		                .collect(Collectors.joining(" "));
		        throw ErrorFactory.validation(errorMessages);
		    }

		    // 6. ESCRIBIR: Guardamos el objeto modificado en la base de datos.
		    User patchedUser = userController.modifyUser(currentUser);

		    // 7. RESPONDER: Enviamos la representación completa y actualizada del recurso.
		    response.setStatus(HttpServletResponse.SC_OK); // 200 OK
		    response.setContentType("application/json;charset=UTF-8");
		    response.getWriter().write(this.gson.toJson(patchedUser));
		}	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {

	    // 1. OBTENER ID: Sacamos el ID del parámetro de la URL.
	    String idParam = request.getParameter("id");
	    int id = Integer.parseInt(idParam);
	    //Crear objeto user con id
	    User userToDelete = new User();
	    userToDelete.setId(id);
	    // 2. ACTUAR: Llamamos al controller para borrar.
	    userController.removeUser(userToDelete); // El método no devuelve nada (void)

	    // 3. RESPONDER: Un borrado exitoso devuelve un 204 sin contenido en el body.
	    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}