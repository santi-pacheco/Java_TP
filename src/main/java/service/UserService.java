package service;

import java.util.List;
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

        if (user == null) {
            throw ErrorFactory.unauthorized("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw ErrorFactory.unauthorized("Invalid username or password");
        }
        
        user.setPassword(null);    
        return user;
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
    
    public List<User> searchUsers(String query) {
        return userRepository.searchUsersByUsername(query);
    }

    public void updatePlatoPrincipal(int userId, Integer movieId) {
        userRepository.updatePlatoPrincipal(userId, movieId);
    }
}