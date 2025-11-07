package service;

import repository.ConfiguracionReglasRepository;
import repository.ReviewRepository;
import repository.UserRepository;
import entity.ConfiguracionReglas;
import entity.User;

import java.util.List;
public class ConfiguracionReglasService {

	private ConfiguracionReglasRepository configuracionReglasRepository;
	
	public ConfiguracionReglasService(ConfiguracionReglasRepository configuracionReglasRepository) {
		this.configuracionReglasRepository = configuracionReglasRepository;
	}	
	
	public ConfiguracionReglas getConfiguracionReglas() {
		return configuracionReglasRepository.getLast();
	}
	
	public ConfiguracionReglas addConfiguracionReglas(ConfiguracionReglas config) {
		ConfiguracionReglas result = configuracionReglasRepository.add(config);
	    // Revalidar todos los usuarios con la nueva configuración
	    revalidateAllUsers(config.getUmbralResenasActivo());
	    return result;
	}
	
	public List<ConfiguracionReglas> getAllConfiguraciones() {
		
	    return configuracionReglasRepository.getAll();
	} 
	
	private void revalidateAllUsers(int newThreshold) {
	    // Implementar la lógica de revalidación
	    UserRepository userRepo = new UserRepository();
	    ReviewRepository reviewRepo = new ReviewRepository();
	    
	    List<User> allUsers = userRepo.findAll();
	    for (User user : allUsers) {
	        int reviewCount = reviewRepo.countReviewsByUser(user.getId());
	        boolean shouldBeActive = reviewCount >= newThreshold;
	        
	        if (user.isEsUsuarioActivo() != shouldBeActive) {
	            userRepo.updateActiveStatus(user.getId(), shouldBeActive);
	        }
	    }
	}
	
}
