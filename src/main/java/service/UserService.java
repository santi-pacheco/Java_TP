package service;
import java.util.List;

import entity.User;
import repository.MovieRepository;
import repository.UserRepository;
import exception.ErrorFactory;

public class UserService {

	private UserRepository userRepository;
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public User getUserById(int id) {
		User user = userRepository.findOne(id);
		if (user == null) {
			throw ErrorFactory.notFound("User not found with ID: " + id);
		}
		return user;
	}
	public User CreateUser(User user) {
		return userRepository.add(user);
	}
	public User updateUser(User user) {
		// 1. Primero, verifica que el usuario exista
	    User existingUser = userRepository.findOne(user.getId());
	    if (existingUser == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Usuario con ID " + user.getId() + " no encontrado.");
	    }
	    // 2. Si existe, ahora sí actualiza
	    return userRepository.update(user);	
	}
	
	public User deleteUser(User user) {
		User userToDelete = userRepository.delete(user);
		return userToDelete;
	}
}
