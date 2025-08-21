package servlet;

import java.io.IOException;
import java.sql.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import controller.UserController;
import entity.User;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet{
	
	
	private static final long serialVersionUID = 1L;
	private UserController userController;

	@Override
	public void init() throws ServletException {
		super.init();
		userController = new UserController();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String idParam = request.getParameter("id");
		
		// Determinar si es una solicitud AJAX o una solicitud normal
		String acceptHeader = request.getHeader("Accept");
		String xRequestedWith = request.getHeader("X-Requested-With");
		boolean isAjaxRequest = (xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")) || 
                            (acceptHeader != null && acceptHeader.contains("application/json"));
		
		// Obtener la lista de usuarios para cualquier tipo de solicitud
		List<User> users = userController.getUsers();
		
		// Si es una solicitud AJAX, devuelve JSON
		if (isAjaxRequest) {
			response.setContentType("application/json;charset=UTF-8");
			Gson gson = new Gson();
			
			if (idParam != null) {
				try {
					int id = Integer.parseInt(idParam);
					User user = userController.getUserById(id);
					if (user != null) {
						String json = gson.toJson(user);
						response.getWriter().write(json);
					} else {
						response.setStatus(HttpServletResponse.SC_NOT_FOUND);
						response.getWriter().write(gson.toJson("Usuario no encontrado"));
					}
				} catch (NumberFormatException e) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().write(gson.toJson("ID inválido"));
				}
			} else {
				String json = gson.toJson(users);
				response.getWriter().write(json);
			}
		} 
		// Si es una solicitud normal, redirige a JSP
		else {
			// Establecer atributos para la vista JSP
			request.setAttribute("users", users);
			
			if (idParam != null) {
				try {
					int id = Integer.parseInt(idParam);
					User user = userController.getUserById(id);
					if (user != null) {
						request.setAttribute("searchedUser", user);
					}
				} catch (NumberFormatException e) {
					// Ignorar error de formato
				}
			}
			
			// Establecer el tipo de contenido para HTML
			response.setContentType("text/html;charset=UTF-8");
			
			// Reenviar al JSP usando ruta absoluta desde el context root
			request.getRequestDispatcher("/userCrud.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String Username = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String role = request.getParameter("role");
		String birthDateStr = request.getParameter("birthDate");
		Date birthDate = null;
		if (birthDateStr != null && !birthDateStr.isEmpty()) {
			try {
				birthDate = Date.valueOf(birthDateStr); // birthDateStr debe estar en formato yyyy-MM-dd
			} catch (IllegalArgumentException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("Formato de fecha inválido");
				return;
			}
		}
		
		if (Username != null && email != null && password != null && role != null && birthDate != null) {
			User newUser = new User();
			newUser.setUsername(Username);
			newUser.setEmail(email);
			newUser.setPassword(password);
			newUser.setRole(role);
			newUser.setBirthDate(birthDate);
			boolean created = userController.createUser(newUser);
			
			if (created) {
				// Verifica si la solicitud es AJAX (desde JavaScript)
				String acceptHeader = request.getHeader("Accept");
				String xRequestedWith = request.getHeader("X-Requested-With");
				
				if ((xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")) || 
					(acceptHeader != null && acceptHeader.contains("application/json"))) {
					// Es una solicitud AJAX, devolver respuesta JSON
					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_CREATED);
					response.getWriter().write("{\"success\": true, \"message\": \"Usuario creado correctamente\"}");
				} else {
					// Es una solicitud normal (no AJAX), redirigir a la página de listado
					response.sendRedirect("users");
				}
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().write("Error al crear usuario");
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Faltan parámetros");
		}
	}
}