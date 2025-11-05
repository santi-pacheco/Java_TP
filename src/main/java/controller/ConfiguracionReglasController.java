package controller;

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
	
}
