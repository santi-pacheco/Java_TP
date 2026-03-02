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
        if (name == null || name.trim().isEmpty()) {
            throw ErrorFactory.badRequest("El código ISO no puede estar vacío.");
        }
        return countryRepository.findOneByISO(name.trim());
    }
    
    public Country createCountry(Country c) {
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            throw ErrorFactory.badRequest("El nombre del país es obligatorio.");
        }
        if (c.getIsoCode() == null || c.getIsoCode().trim().isEmpty()) {
            throw ErrorFactory.badRequest("El código ISO del país es obligatorio.");
        }
        return countryRepository.add(c);
    }
    
    public Country updateCountry(Country c) {
        if (c.getName() == null || c.getName().trim().isEmpty() || c.getIsoCode() == null || c.getIsoCode().trim().isEmpty()) {
            throw ErrorFactory.badRequest("El nombre y el código ISO son obligatorios.");
        }
        
        Country existingCountry = countryRepository.findOne(c.getCountryId());
        if (existingCountry == null) {
            throw ErrorFactory.notFound("No se puede actualizar. País con ID " + c.getCountryId() + " no encontrado.");
        }
        
        existingCountry.setName(c.getName().trim());
        existingCountry.setIsoCode(c.getIsoCode().trim());
        return countryRepository.update(existingCountry);
    }
    
    public void deleteCountry(Country c) {
        if (c == null || c.getCountryId() <= 0) {
            throw ErrorFactory.badRequest("País inválido para eliminar.");
        }
        countryRepository.delete(c);
    }
    
    public void saveAllCountries(List<Country> countries) {
        if (countries != null && !countries.isEmpty()) {
            countryRepository.saveAll(countries);
        }
    }
    
    public Map<String, Integer> getMapIds(List<String> isoCodes) {
        return countryRepository.getMapIds(isoCodes);
    }
    
    public void saveBatchRelations(List<Object[]> relations) {
        if (relations != null && !relations.isEmpty()) {
            countryRepository.saveBatchRelations(relations);
        }
    }
}