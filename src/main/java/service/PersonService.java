package service;

import entity.Person;
import entity.ActorWithCharacter;
import repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import exception.ErrorFactory;
import java.util.Map;

public class PersonService {
    
    private PersonRepository personRepository;
    
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
    
    public Person getPersonById(int id) {
        if (id <= 0) {
            throw ErrorFactory.badRequest("ID de persona inválido.");
        }
        Person person = personRepository.findOne(id);
        if (person == null) {
            throw ErrorFactory.notFound("Person not found with ID: " + id);
        }
        return person;
    }
    
    public Person createPerson(Person per) {
        if (per == null || per.getName() == null || per.getName().trim().isEmpty()) {
            throw ErrorFactory.badRequest("Datos de persona inválidos para crear.");
        }
        return personRepository.add(per);
    }
    
    public Person updatePerson(Person per) {
        if (per == null || per.getPersonId() <= 0) {
            throw ErrorFactory.badRequest("Datos inválidos para actualizar persona.");
        }
        
        Person existingPerson = personRepository.findOne(per.getPersonId());
        if (existingPerson == null) {
            throw ErrorFactory.notFound("No se puede actualizar. Persona con ID " + per.getPersonId() + " no encontrada.");
        }
        
        existingPerson.setName(per.getName());
        existingPerson.setAlsoKnownAs(per.getAlsoKnownAs());
        existingPerson.setPlaceOfBirth(per.getPlaceOfBirth());
        existingPerson.setBirthdate(per.getBirthdate());
        
        return personRepository.update(existingPerson);
    }
    
    public void deletePerson(Person per) {
        if (per != null && per.getPersonId() > 0) {
            personRepository.delete(per);
        }
    }
    
    public List<Person> saveDirectors(List<Person> directors) {
        if (directors == null || directors.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Person> savedDirectors = new ArrayList<>();
        for (Person dir : directors) {
            if (dir == null) continue;
            
            Person existingPerson = personRepository.findByApiId(dir.getApiId());
            if (existingPerson != null) {
                savedDirectors.add(existingPerson);
            } else {
                Person p = personRepository.add(dir);
                savedDirectors.add(p);
            }
        }
        return savedDirectors;
    }
    
    public List<ActorWithCharacter> getActorsByMovieId(int movieId) {
        if (movieId <= 0) return new ArrayList<>();
        return personRepository.findActorsByMovieId(movieId);
    }
    
    public List<Person> getDirectorsByMovieId(int movieId) {
        if (movieId <= 0) return new ArrayList<>();
        return personRepository.findDirectorsByMovieId(movieId);
    }
    
    public void updateAllPersonsbyId_api(List<Person> persons) {
        if (persons != null && !persons.isEmpty()) {
            personRepository.updateAllPersonsbyId_api(persons);
        }
    }
        
    public void saveAllPersons(List<Person> persons) {
        if (persons != null && !persons.isEmpty()) {
            personRepository.saveAll(persons);
        }
    }
    
    public Map<Integer, Integer> getMapIds(List<Integer> apiIds) {
        if (apiIds == null || apiIds.isEmpty()) {
            return Map.of(); // Devuelve mapa vacío
        }
        return personRepository.getMapIds(apiIds);
    }
}