package controller;

import service.CountryService;
import repository.CountryRepository;
import entity.Country;

import java.util.List;

public class CountryController {
	
	private CountryService countryService;
	
	public CountryController() {
	
		CountryRepository countryRepository = new CountryRepository();
		this.countryService = new CountryService(countryRepository);
		
	}
	
	public List<Country> getCountries() {
		try {
			List<Country> contries = countryService.getAllCountries();
			System.out.println("Países obtenidos exitosamente: " + contries.size() + " registros");
			return contries;
		} catch (Exception e) {
			System.err.println("Error al obtener países: " + e.getMessage());
			throw new RuntimeException("Error getting countries", e);
		}
	}
}
