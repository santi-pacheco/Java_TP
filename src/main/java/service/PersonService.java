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
		Person person = personRepository.findOne(id);
		if (person == null) {
			throw ErrorFactory.notFound("Person not found with ID: " + id);
		}
		return person;
	}
	
	public Person createPerson(Person per) {
		Person person = personRepository.add(per);
		return person;
	}
	
	public Person updatePerson(Person per) {
		// 1. Primero, verifica que la persona exista
	    Person existingPerson = personRepository.findOne(per.getPersonId());
	    if (existingPerson == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Persona con ID " + per.getPersonId() + " no encontrada.");
	    }
	    
	    existingPerson.setName(per.getName());
	    existingPerson.setAlsoKnownAs(per.getAlsoKnownAs());
	    existingPerson.setPlaceOfBirth(per.getPlaceOfBirth());
	    existingPerson.setBirthdate(per.getBirthdate());
	    
	    // 2. Si existe, ahora sí actualiza
	    return personRepository.update(existingPerson);
	}
	
	public void deletePerson(Person per) {
		personRepository.delete(per);
	}
	
		public List<Person> saveDirectors(List<Person> directors) {
			//Verificar que la persona no exista ya en la base de datos antes de guardarla
			List<Person> savedDirectors = new ArrayList<>();
			for (Person dir : directors) {
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
			return personRepository.findActorsByMovieId(movieId);
		}
		
		public List<Person> getDirectorsByMovieId(int movieId) {
			return personRepository.findDirectorsByMovieId(movieId);
		}
		
		public void updateAllPersonsbyId_api(List<Person> persons) {
			personRepository.updateAllPersonsbyId_api(persons);
		}
		
	public void saveAllPersons(List<Person> persons) {
		personRepository.saveAll(persons);
	}
	
	public Map<Integer, Integer> getMapIds(List<Integer> apiIds) {
	    return personRepository.getMapIds(apiIds);
	}
}
