package controller;

import java.util.List;
import entity.User;
import repository.UserRepository;
import service.UserService;

public class UserController {

	private UserService userService;
	
	public UserController() {
		try {
		// Crear UserRepository sin pasar una conexión, ya que ahora maneja sus propias conexiones
		UserRepository userRepository = new UserRepository();
		this.userService = new UserService(userRepository);
		}
		catch (Exception e) {
			System.err.println("Error al inicializar UserController: " + e.getMessage());
			throw new RuntimeException("Error initializing UserController", e);
		}
	}
	public List<User> getUsers(){
		try {
			List<User> users = userService.getAllUsers();
			System.out.println("Usuarios obtenidos exitosamente: " + users.size() + " registros");
			return users;
		} catch (Exception e) {
			System.err.println("Error al obtener usuarios: " + e.getMessage());
			throw new RuntimeException("Error getting users", e);
		}
	}
	public User getUserById(int id) {
		try {
			User user = userService.getUserById(id);
			if (user != null) {
				System.out.println("Usuario obtenido exitosamente: " + user.getUsername());
			} else {
				System.out.println("Usuario no encontrado con ID: " + id);
			}
			return user;
		} catch (Exception e) {
			System.err.println("Error al obtener usuario por ID: " + e.getMessage());
			throw new RuntimeException("Error getting user by ID", e);
		}
		}
	public boolean createUser(User user) {
	    try {
	        User createdUser = userService.CreateUser(user);
	        if (createdUser != null) {
	            System.out.println("Usuario creado exitosamente: " + createdUser.getUsername());
	            return true;
	        } else {
	            System.out.println("No se pudo crear el usuario.");
	            return false;
	        }
	    } catch (Exception e) {
	        System.err.println("Error al crear usuario: " + e.getMessage());
	        throw new RuntimeException("Error creating user", e);
	    }
	}

	}