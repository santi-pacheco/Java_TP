package entity;

import jakarta.validation.constraints.Min;

public class ConfiguracionReglas {

    private int configId;

    @Min(value = 1, message = "El umbral del Nivel 2 debe ser al menos 1")
    private int kcalsToLevel2;

    @Min(value = 1, message = "El umbral del Nivel 3 debe ser al menos 1")
    private int kcalsToLevel3;

    @Min(value = 1, message = "El umbral del Nivel 4 debe ser al menos 1")
    private int kcalsToLevel4;

    @Min(value = 1, message = "El límite de watchlist debe ser al menos 1")
    private int normalWatchlistLimit;

    @Min(value = 1, message = "El límite de watchlist activo debe ser al menos 1")
    private int activeWatchlistLimit;

    private String effectiveDate;

    private Integer adminUserId;

    public int getConfigId() { return configId; }
    public void setConfigId(int configId) { this.configId = configId; }

    public int getKcalsToLevel2() { return kcalsToLevel2; }
    public void setKcalsToLevel2(int kcalsToLevel2) { this.kcalsToLevel2 = kcalsToLevel2; }

    public int getKcalsToLevel3() { return kcalsToLevel3; }
    public void setKcalsToLevel3(int kcalsToLevel3) { this.kcalsToLevel3 = kcalsToLevel3; }

    public int getKcalsToLevel4() { return kcalsToLevel4; }
    public void setKcalsToLevel4(int kcalsToLevel4) { this.kcalsToLevel4 = kcalsToLevel4; }

    public int getNormalWatchlistLimit() { return normalWatchlistLimit; }
    public void setNormalWatchlistLimit(int normalWatchlistLimit) { this.normalWatchlistLimit = normalWatchlistLimit; }

    public int getActiveWatchlistLimit() { return activeWatchlistLimit; }
    public void setActiveWatchlistLimit(int activeWatchlistLimit) { this.activeWatchlistLimit = activeWatchlistLimit; }

    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }

    public Integer getAdminUserId() { return adminUserId; }
    public void setAdminUserId(Integer adminUserId) { this.adminUserId = adminUserId; }
}
