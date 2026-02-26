package service;

import repository.ConfiguracionReglasRepository;
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
        revalidateAllUsers(config);
        return result;
    }
    
    public List<ConfiguracionReglas> getAllConfiguraciones() {
        return configuracionReglasRepository.getAll();
    } 
    
    private void revalidateAllUsers(ConfiguracionReglas config) {
        UserRepository userRepo = new UserRepository();
        List<User> allUsers = userRepo.findAll();
        
        for (User user : allUsers) {
            int kcals = user.getTotalKcals();
            int newLevel = 1;
            
            if (kcals >= config.getUmbralKcalsNivel4()) {
                newLevel = 4;
            } else if (kcals >= config.getUmbralKcalsNivel3()) {
                newLevel = 3;
            } else if (kcals >= config.getUmbralKcalsNivel2()) {
                newLevel = 2;
            }
            
            if (user.getNivelUsuario() != newLevel) {
                userRepo.updateUserVolume(user.getId(), kcals, newLevel);
            }
        }
    }
}