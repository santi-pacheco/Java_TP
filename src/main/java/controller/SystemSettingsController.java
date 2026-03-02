package controller;

import java.util.List;
import service.SystemSettingsService;
import entity.SystemSettings;

public class SystemSettingsController {

    private SystemSettingsService systemSettingsService;

    public SystemSettingsController(SystemSettingsService systemSettingsService) {
        this.systemSettingsService = systemSettingsService;
    }

    public SystemSettings getSystemSettings() {
        return systemSettingsService.getSystemSettings();
    }

    public SystemSettings addSystemSettings(SystemSettings config) {
        return systemSettingsService.addSystemSettings(config);
    }

    public List<SystemSettings> getAllSystemSettings() {
        return systemSettingsService.getAllSystemSettings();
    }
}
