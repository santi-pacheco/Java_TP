package service;
import java.util.List;
import repository.BlockRepository;
import java.util.UUID;

import entity.User;
import repository.FollowRepository;
import repository.UserRepository;
import exception.ErrorFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.io.File;

public class UserService {

	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private FollowRepository followRepository;
	private BlockRepository blockRepository;
	
	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, FollowRepository followRepository, BlockRepository blockRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.followRepository = followRepository;
		this.blockRepository = blockRepository;
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
	    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
	        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
	        existingUser.setPassword(hashedPassword);
	    }
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
	
	public void updateProfileImage(int userId, String newFileName, String uploadDir) {
	    User user = userRepository.findOne(userId);
	    if (user != null && user.getProfileImage() != null && !user.getProfileImage().isEmpty()) { 
	        File oldFile = new File(uploadDir + File.separator + user.getProfileImage());
	        if (oldFile.exists()) {
	            boolean deleted = oldFile.delete();
	            if (!deleted) {
	                System.err.println("ADVERTENCIA: No se pudo borrar la imagen vieja: " + oldFile.getAbsolutePath());
	            }
	        }
	    }
	    userRepository.updateProfileImage(userId, newFileName);
	}

    public void removeProfileImage(int userId, String uploadDir) {
        User user = userRepository.findOne(userId);

        if (user != null && user.getProfileImage() != null) {
            File file = new File(uploadDir + File.separator + user.getProfileImage());
            if (file.exists()) {
                file.delete();
            }
            userRepository.updateProfileImage(userId, null);
        }
    }
    
    public List<User> searchUsers(String query, int loggedUserId) {
		return userRepository.searchUsersByUsername(query, loggedUserId);
	}

    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email);
        System.out.println("Usuario encontrado para email " + email + ": " + (user != null ? user.getUsername() : "null"));
        if (user == null) {
            return null; 
        }
        String token = UUID.randomUUID().toString();
        userRepository.savePasswordResetToken(user.getId(), token);
        return token;
    }
    
    public boolean validateResetToken(String token) {
        if (token == null || token.isEmpty()) return false;
        return userRepository.getUserIdByValidToken(token) != null;
    }
    
    public void resetPasswordWithToken(String token, String newPassword) {
        Integer userId = userRepository.getUserIdByValidToken(token);
        
        if (userId == null) {
            throw ErrorFactory.badRequest("El enlace de recuperación es inválido o ha expirado.");
        }
        
        String hashedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePasswordAndClearToken(userId, hashedPassword, token);
    }
    
    public void toggleBlock(int blockerId, int blockedId) {
        if (blockerId == blockedId) {
            throw ErrorFactory.badRequest("No puedes bloquearte a ti mismo.");
        }
        
        boolean isBlocked = blockRepository.isBlocking(blockerId, blockedId);
        if (isBlocked) {
            blockRepository.removeBlock(blockerId, blockedId);
        } else {
            blockRepository.addBlock(blockerId, blockedId);
            if (followRepository.isFollowing(blockerId, blockedId)) {
                followRepository.removeFollow(blockerId, blockedId);
            }
            if (followRepository.isFollowing(blockedId, blockerId)) {
                followRepository.removeFollow(blockedId, blockerId);
            }
        }
    }
	
	public boolean isBlocking(int blockerId, int blockedId) {
        return blockRepository.isBlocking(blockerId, blockedId);
    }
	
	public List<User> getBlockedUsers(int blockerId) {
	    return blockRepository.getBlockedUsers(blockerId);
	}
	
}