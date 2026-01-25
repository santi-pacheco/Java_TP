package service;
import java.util.List;

import entity.User;
import repository.FollowRepository;
import repository.UserRepository;
import exception.ErrorFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserService {

	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private FollowRepository followRepository;
	
	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, FollowRepository followRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.followRepository = followRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public User getUserById(int id) {
		User user = userRepository.findOne(id);
		if (user == null) {
			throw ErrorFactory.notFound("User not found with ID: " + id);
		}
		//Pongo las pass en null
		user.setPassword(null);
		return user;
	}
	
	public User CreateUser(User user) {
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
		return userRepository.add(user);
	}
	
	public User updateUser(User user) {
	    User existingUser = userRepository.findOne(user.getId());
	    if (existingUser == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Usuario con ID " + user.getId() + " no encontrado.");
	    }
	    existingUser.setUsername(user.getUsername());
	    existingUser.setEmail(user.getEmail());
	    existingUser.setRole(user.getRole());
	    existingUser.setBirthDate(user.getBirthDate());
	    existingUser.setEsUsuarioActivo(user.isEsUsuarioActivo());

	    return userRepository.update(existingUser);
	}
	
	public void deleteUser(User user) {
		userRepository.delete(user);
	}
	
	public User authenticateUser(String username, String password) {
		User user;
	    // --- 1. Buscar al usuario ---
	    // Buscamos al usuario por su nombre de usuario en la BD
		user = userRepository.findByUsername(username);

	    // --- 2. Validar si el usuario existe ---
	    // Guiándonos por getUserById: si es nulo, lanzamos un error.
		if (user == null) {
			// ERROR RECUPERABLE (401)
			// Lanzamos "Unauthorized". Esto lo capturará el LoginServlet.
			throw ErrorFactory.unauthorized("Invalid username or password");
		}

	    // --- 3. Validar la contraseña ---
	    // Usamos el passwordEncoder (como en createUser) para comparar
	    // la clave plana (password) con la hasheada (user.getPassword())
		if (!passwordEncoder.matches(password, user.getPassword())) {
	        // ERROR RECUPERABLE (401)
	        // Usamos el *mismo* mensaje para no dar pistas
			throw ErrorFactory.unauthorized("Invalid username or password");
		}
	    // --- 4. ÉXITO: Preparar el retorno ---
	    // Siguiendo el patrón de getUserById, quitamos la contraseña
	    // antes de devolver el objeto.
		user.setPassword(null);	
		return user;
	}
	
	public void checkAndUpdateActiveStatus(int userId, int umbralResenas) {
		User user = userRepository.findOne(userId);
		if (user != null && !user.isEsUsuarioActivo()) {
			// Solo verificar si no es activo actualmente
			int reviewCount = getReviewCountForUser(userId);
			if (reviewCount >= umbralResenas) {
				userRepository.updateActiveStatus(userId, true);
			}
		}
	}
	
	public void validateActiveStatusOnDelete(int userId, int umbralResenas) {
		User user = userRepository.findOne(userId);
		if (user != null && user.isEsUsuarioActivo()) {
			// Solo verificar si es activo actualmente
			int reviewCount = getReviewCountForUser(userId);
			if (reviewCount < umbralResenas) {
				userRepository.updateActiveStatus(userId, false);
			}
		}
	}
	
	private int getReviewCountForUser(int userId) {
		// Este método necesita acceso al ReviewRepository
		// Se implementará en ReviewService
		return 0;
	}
	
	public void toggleFollow(int currentUserId, int targetUserId) {
        if (currentUserId == targetUserId) {
            throw ErrorFactory.badRequest("No puedes seguirte a ti mismo.");
        }
        boolean isFollowing = followRepository.isFollowing(currentUserId, targetUserId);

        if (isFollowing) {
            followRepository.removeFollow(currentUserId, targetUserId);
        } else {
            followRepository.addFollow(currentUserId, targetUserId);
        }
    }
	
	public boolean isFollowing(int currentUserId, int targetUserId) {
        return followRepository.isFollowing(currentUserId, targetUserId);
    }
	
	public List<User> getFollowers(int userId) {
	    return followRepository.findFollowers(userId);
	}

	public List<User> getFollowing(int userId) {
	    return followRepository.findFollowing(userId);
	}
}