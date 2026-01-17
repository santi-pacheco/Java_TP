package controller;

import service.CountryService;
import entity.Country;
import java.util.Map;

import java.util.List;

public class CountryController {
	
	private CountryService countryService;

	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}
	
	public List<Country> getCountries() {
		List<Country> contries = countryService.getAllCountries();
		System.out.println("Países obtenidos exitosamente: " + contries.size() + " registros");
		return contries;
	}
	
	public Country getCountryById(int id) {
		Country country = countryService.getCountryById(id);
		return country;
	}
	
	public Country createCountry(Country country) {
		return countryService.createCountry(country);
	}
	
	public Country modifyCountry(Country country) {
		return countryService.updateCountry(country);
	}
	
	public void removeCountry(Country country) {
		countryService.deleteCountry(country);
	}
	
	public void saveAllCountries(List<Country> countries) {
		countryService.saveAllCountries(countries);
	}
	
	public Map<String, Integer> getMapIds(List<String> isoCodes) {
		return countryService.getMapIds(isoCodes);
	}
	
	public void saveBatchRelations(List<Object[]> relations) {
		countryService.saveBatchRelations(relations);
	}
}
