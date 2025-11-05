package entity;

public class ConfiguracionReglas {

	private int configID;
	
	private int UmbralResenasActivo;
	
	private int limiteWatchlistNormal;
	
	private int limiteWatchlistActivo;
	
	private String fechaVigencia;
	
	private Integer usuarioAdminID;

	public int getConfigID() {
		return configID;
	}

	public void setConfigID(int configID) {
		this.configID = configID;
	}

	public int getUmbralResenasActivo() {
		return UmbralResenasActivo;
	}

	public void setUmbralResenasActivo(int umbralResenasActivo) {
		UmbralResenasActivo = umbralResenasActivo;
	}

	public int getLimiteWatchlistNormal() {
		return limiteWatchlistNormal;
	}

	public void setLimiteWatchlistNormal(int limiteWatchlistNormal) {
		this.limiteWatchlistNormal = limiteWatchlistNormal;
	}

	public int getLimiteWatchlistActivo() {
		return limiteWatchlistActivo;
	}

	public void setLimiteWatchlistActivo(int limiteWatchlistActivo) {
		this.limiteWatchlistActivo = limiteWatchlistActivo;
	}

	public String getFechaVigencia() {
		return fechaVigencia;
	}

	public void setFechaVigencia(String fechaVigencia) {
		this.fechaVigencia = fechaVigencia;
	}

	public Integer getUsuarioAdminID() {
		return usuarioAdminID;
	}

	public void setUsuarioAdminID(Integer usuarioAdminID) {
		this.usuarioAdminID = usuarioAdminID;
	}
}
