package service;
import java.util.List;
import entity.User;
import repository.UserRepository;

public class UserService {

	private UserRepository userRepository;
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public User getUserById(int id) {
		return userRepository.findOne(id);
	}
	public User CreateUser(User user) {
		if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
			return userRepository.add(user);
		} else {
			throw new IllegalArgumentException("Invalid user data");
		}
	}
}
