package service;

import repository.ConfiguracionReglasRepository;
import entity.ConfiguracionReglas;
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
		return configuracionReglasRepository.add(config);
	}
	
	public List<ConfiguracionReglas> getAllConfiguraciones() {
	    return configuracionReglasRepository.getAll();
	} 
	
}
