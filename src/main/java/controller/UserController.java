package controller;

import java.util.List;

import entity.User;
import service.UserService;

public class UserController {

	private UserService userService;
	
	public UserController(UserService userService) {
		//Ahora el repository no necesita la conexión en el constructor, ya no se encarga de eso
		this.userService = userService;
	}
	public List<User> getUsers(){
			List<User> users = userService.getAllUsers();
			System.out.println("Usuarios obtenidos exitosamente: " + users.size() + " registros");
			return users;
	}
	public User getUserById(int id) {
			User user = userService.getUserById(id);
			return user;

	}
	public User createUser(User user) {
	        return userService.CreateUser(user);
	}
	
	public User modifyUser(User user) {
			return userService.updateUser(user);
	}
	
	public void removeUser(User user) {
			userService.deleteUser(user);
	}
	
}