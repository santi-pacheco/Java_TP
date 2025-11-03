package service;

import entity.Person;
import entity.ActorWithCharacter;

import repository.PersonRepository;
import util.DiscoverReflectionMain.actorCharacter;

import java.util.ArrayList;
import java.util.List;
import exception.ErrorFactory;

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
	    Person existingPerson = personRepository.findOne(per.getId());
	    if (existingPerson == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Persona con ID " + per.getId() + " no encontrada.");
	    }
	    
	    existingPerson.setName(per.getName());
	    existingPerson.setAlso_known_as(per.getAlso_known_as());
	    existingPerson.setPlace_of_birth(per.getPlace_of_birth());
	    existingPerson.setBirthDate(per.getBirthDate());
	    
	    // 2. Si existe, ahora sí actualiza
	    return personRepository.update(existingPerson);
	}
	
	public void deletePerson(Person per) {
		personRepository.delete(per);
	}
	
	public List<actorCharacter> saveActors(List <service.ExternalApiService.PersonWithCharacter> personWithCharacter){
		//Verificar que la persona no exista ya en la base de datos antes de guardarla
		List<actorCharacter> actorCharacters = new ArrayList<>();
		for (service.ExternalApiService.PersonWithCharacter pwc : personWithCharacter) {
			Person existingPerson = personRepository.findByApiId(pwc.getId_api());
			if (existingPerson != null) {
				actorCharacter ac = new actorCharacter(existingPerson.getId(), pwc.getCharacterName());
				actorCharacters.add(ac);
			} else {
				Person newPerson = new Person();
				newPerson.setId_api(pwc.getId_api());
				newPerson.setName(pwc.getName());
				newPerson.setAlso_known_as(pwc.getAlso_known_as());
				newPerson.setPlace_of_birth(pwc.getPlace_of_birth());
				newPerson.setBirthDate(pwc.getBirthDate());
				System.out.println("Saving new person: " + newPerson);
				Person savedPerson = personRepository.add(newPerson);
				actorCharacter ac = new actorCharacter(savedPerson.getId(), pwc.getCharacterName());
				actorCharacters.add(ac);
			}
		}
		return actorCharacters;
	}
	
		public List<Person> saveDirectors(List<Person> directors) {
			//Verificar que la persona no exista ya en la base de datos antes de guardarla
			List<Person> savedDirectors = new ArrayList<>();
			for (Person dir : directors) {
				Person existingPerson = personRepository.findByApiId(dir.getId_api());
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
}
