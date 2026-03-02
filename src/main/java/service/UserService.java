package service;

import java.util.List;
import java.util.UUID;
import java.io.File;

import entity.User;
import repository.UserRepository;
import repository.FollowRepository;
import repository.BlockRepository;
import exception.ErrorFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
            throw ErrorFactory.notFound("Usuario no encontrado con ID: " + id);
        }
        user.setPassword(null); // Seguridad
        return user;
    }
    
    public User CreateUser(User user) {
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.add(user);
    }
    
    public User updateUser(User user) {
        User existingUser = userRepository.findOne(user.getUserId());
        if (existingUser == null) {
             throw ErrorFactory.notFound("No se puede actualizar. Usuario con ID " + user.getUserId() + " no encontrado.");
        }
        
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setBirthDate(user.getBirthDate());
        
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
        User user = userRepository.findByUsername(username);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw ErrorFactory.unauthorized("Usuario o contraseña inválidos.");
        }
        
        // Se quitó la validación del BAN de aquí. El usuario puede iniciar sesión en modo "solo lectura".
        
        user.setPassword(null); 
        return user;
    }
    
    public void toggleFollow(int currentUserId, int targetUserId) {
        if (currentUserId == targetUserId) {
            throw ErrorFactory.badRequest("No puedes seguirte a ti mismo.");
        }
        if (blockRepository.isBlocking(currentUserId, targetUserId) || 
            blockRepository.isBlocking(targetUserId, currentUserId)) {
            throw ErrorFactory.badRequest("No puedes seguir a este usuario debido a un bloqueo mutuo.");
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
                oldFile.delete(); // Borrado silencioso sin ensuciar la consola
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
        if (query == null || query.trim().isEmpty()) {
            throw ErrorFactory.badRequest("La búsqueda no puede estar vacía.");
        }
        return userRepository.searchUsersByUsername(query.trim(), loggedUserId);
    }

    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        
        String token = UUID.randomUUID().toString();
        userRepository.savePasswordResetToken(user.getUserId(), token);
        return token;
    }
    
    public boolean validateResetToken(String token) {
        if (token == null || token.trim().isEmpty()) return false;
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

    public void updatePlatoPrincipal(int userId, Integer movieId) {
        User u = userRepository.findOne(userId);
        if (u != null && u.getUserLevel() >= 3) {
            userRepository.updatePlatoPrincipal(userId, movieId);
        } else {
            throw ErrorFactory.unauthorized("Necesitas ser Nivel 3 o superior para elegir un Plato Principal.");
        }  
    }
    
    public void markLevelAsNotified(int userId, int newLevel) {
        userRepository.markLevelAsNotified(userId, newLevel);
    }
}