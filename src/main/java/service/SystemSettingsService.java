package service;

import repository.SystemSettingsRepository;
import repository.UserRepository;
import entity.SystemSettings;
import entity.User;
import exception.ErrorFactory;

import java.util.List;

public class SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;
    private final UserRepository userRepository;

    public SystemSettingsService(SystemSettingsRepository systemSettingsRepository, UserRepository userRepository) {
        this.systemSettingsRepository = systemSettingsRepository;
        this.userRepository = userRepository;
    }
    

    public SystemSettingsService(SystemSettingsRepository systemSettingsRepository) {
        this.systemSettingsRepository = systemSettingsRepository;
        this.userRepository = new UserRepository(); // Fallback temporal para no romper código antiguo
    }

    public SystemSettings getSystemSettings() {
        return systemSettingsRepository.getLast();
    }

    public SystemSettings addSystemSettings(SystemSettings config) {
        if (config == null) {
            throw ErrorFactory.badRequest("La configuración no puede ser nula.");
        }
        if (config.getNormalWatchlistLimit() <= 0 || config.getActiveWatchlistLimit() <= 0) {
            throw ErrorFactory.badRequest("Los límites de la watchlist deben ser mayores a 0.");
        }
        if (config.getKcalsToLevel2() < 0 || config.getKcalsToLevel3() < 0 || config.getKcalsToLevel4() < 0) {
            throw ErrorFactory.badRequest("Los requisitos de calorías no pueden ser negativos.");
        }
        
        SystemSettings result = systemSettingsRepository.add(config);
        revalidateAllUsers(config);
        return result;
    }

    public List<SystemSettings> getAllSystemSettings() {
        return systemSettingsRepository.getAll();
    }

    private void revalidateAllUsers(SystemSettings config) {
        if (config == null) return;
        
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            int kcals = user.getTotalKcals();
            int newLevel = 1;

            if (kcals >= config.getKcalsToLevel4()) {
                newLevel = 4;
            } else if (kcals >= config.getKcalsToLevel3()) {
                newLevel = 3;
            } else if (kcals >= config.getKcalsToLevel2()) {
                newLevel = 2;
            }

            if (user.getUserLevel() != newLevel) {
                userRepository.updateUserVolume(user.getUserId(), kcals, newLevel);
            }
        }
    }
}