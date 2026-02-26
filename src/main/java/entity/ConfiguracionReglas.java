package entity;

import jakarta.validation.constraints.Min;

public class ConfiguracionReglas {

    private int configID;
    
    @Min(value = 1, message = "El umbral del Nivel 2 debe ser al menos 1")
    private int umbralKcalsNivel2;
    
    @Min(value = 1, message = "El umbral del Nivel 3 debe ser al menos 1")
    private int umbralKcalsNivel3;
    
    @Min(value = 1, message = "El umbral del Nivel 4 debe ser al menos 1")
    private int umbralKcalsNivel4;
    
    @Min(value = 1, message = "El límite de watchlist debe ser al menos 1")
    private int limiteWatchlistNormal;
    
    @Min(value = 1, message = "El límite de watchlist activo debe ser al menos 1")
    private int limiteWatchlistActivo;
    
    private String fechaVigencia;
    
    private Integer usuarioAdminID;

    public int getConfigID() { return configID; }
    public void setConfigID(int configID) { this.configID = configID; }

    public int getUmbralKcalsNivel2() { return umbralKcalsNivel2; }
    public void setUmbralKcalsNivel2(int umbralKcalsNivel2) { this.umbralKcalsNivel2 = umbralKcalsNivel2; }

    public int getUmbralKcalsNivel3() { return umbralKcalsNivel3; }
    public void setUmbralKcalsNivel3(int umbralKcalsNivel3) { this.umbralKcalsNivel3 = umbralKcalsNivel3; }

    public int getUmbralKcalsNivel4() { return umbralKcalsNivel4; }
    public void setUmbralKcalsNivel4(int umbralKcalsNivel4) { this.umbralKcalsNivel4 = umbralKcalsNivel4; }

    public int getLimiteWatchlistNormal() { return limiteWatchlistNormal; }
    public void setLimiteWatchlistNormal(int limiteWatchlistNormal) { this.limiteWatchlistNormal = limiteWatchlistNormal; }

    public int getLimiteWatchlistActivo() { return limiteWatchlistActivo; }
    public void setLimiteWatchlistActivo(int limiteWatchlistActivo) { this.limiteWatchlistActivo = limiteWatchlistActivo; }

    public String getFechaVigencia() { return fechaVigencia; }
    public void setFechaVigencia(String fechaVigencia) { this.fechaVigencia = fechaVigencia; }

    public Integer getUsuarioAdminID() { return usuarioAdminID; }
    public void setUsuarioAdminID(Integer usuarioAdminID) { this.usuarioAdminID = usuarioAdminID; }
}