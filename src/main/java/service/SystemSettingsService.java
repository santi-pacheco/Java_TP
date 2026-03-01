package service;

import repository.SystemSettingsRepository;
import repository.UserRepository;
import entity.SystemSettings;
import entity.User;

import java.util.List;

public class SystemSettingsService {

    private SystemSettingsRepository systemSettingsRepository;

    public SystemSettingsService(SystemSettingsRepository systemSettingsRepository) {
        this.systemSettingsRepository = systemSettingsRepository;
    }

    public SystemSettings getSystemSettings() {
        return systemSettingsRepository.getLast();
    }

    public SystemSettings addSystemSettings(SystemSettings config) {
        SystemSettings result = systemSettingsRepository.add(config);
        revalidateAllUsers(config);
        return result;
    }

    public List<SystemSettings> getAllSystemSettings() {
        return systemSettingsRepository.getAll();
    }

    private void revalidateAllUsers(SystemSettings config) {
        UserRepository userRepo = new UserRepository();
        List<User> allUsers = userRepo.findAll();

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
                userRepo.updateUserVolume(user.getUserId(), kcals, newLevel);
            }
        }
    }
}
