package controller;
import java.util.List;
import service.ConfiguracionReglasService;
import entity.ConfiguracionReglas;

public class ConfiguracionReglasController {

	private ConfiguracionReglasService configuracionReglasService;
	
	public ConfiguracionReglasController(ConfiguracionReglasService configuracionReglasService) {
		this.configuracionReglasService = configuracionReglasService;
	}
	
	public ConfiguracionReglas getConfiguracionReglas() {
		return configuracionReglasService.getConfiguracionReglas();
	}
	
	public ConfiguracionReglas addConfiguracionReglas(ConfiguracionReglas config) {
		return configuracionReglasService.addConfiguracionReglas(config);
	}
	
	public List<ConfiguracionReglas> getAllConfiguraciones() {
	    return configuracionReglasService.getAllConfiguraciones();
	}
	
}
