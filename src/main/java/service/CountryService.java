package service;

import repository.CountryRepository;
import java.util.List;

public class CountryService {
	private CountryRepository countryRepository;
	
	public CountryService(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}
	
	public List<entity.Country> getAllCountries() {
		return countryRepository.findAll();
	}
	public entity.Country getCountryById(int id) {
		return countryRepository.findOne(id);
	}
	public entity.Country updateCountry(entity.Country c) {
		if (c != null && c.getEnglish_name() != null && !c.getEnglish_name().isEmpty()) {
			return countryRepository.update(c);
		} else {
			throw new IllegalArgumentException("Invalid country data");
		}
	}
	
}
