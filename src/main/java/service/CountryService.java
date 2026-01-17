package service;

import repository.CountryRepository;
import java.util.List;
import entity.Country;
import exception.ErrorFactory;
import java.util.Map;

public class CountryService {
	private CountryRepository countryRepository;
	
	public CountryService(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}
	
	public List<Country> getAllCountries() {
		return countryRepository.findAll();
	}
	
	public Country getCountryById(int id) {
		Country country = countryRepository.findOne(id);
		if (country == null) {
			throw ErrorFactory.notFound("Country not found with ID: " + id);
		}
		return country;
	}
	
	public int getOneByName(String name) {
		return countryRepository.findOneByISO(name);
	}
	
	public Country createCountry(Country c) {
		return countryRepository.add(c);
	}
	
	public Country updateCountry(Country c) {
		// 1. Primero, verifica que el país exista
	    Country existingCountry = countryRepository.findOne(c.getId());
	    if (existingCountry == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. País con ID " + c.getId() + " no encontrado.");
	    }
	    existingCountry.setEnglish_name(c.getEnglish_name());
	    existingCountry.setIso_3166_1(c.getIso_3166_1());

	    // 2. Si existe, ahora sí actualiza
	    return countryRepository.update(existingCountry);
	}
	
	public void deleteCountry(Country c) {
		countryRepository.delete(c);
	}
	
	public void saveAllCountries(List<Country> countries) {
		countryRepository.saveAll(countries);
	}
	
	public Map<String, Integer> getMapIds(List<String> isoCodes) {
		return countryRepository.getMapIds(isoCodes);
	}
	
	public void saveBatchRelations(List<Object[]> relations) {
		countryRepository.saveBatchRelations(relations);
	}
}
