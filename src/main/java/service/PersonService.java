package service;

import entity.Person;
import repository.PersonRepository;
import java.util.List;
import exception.ErrorFactory;

public class PersonService {
	
	//private ExternalApiService externalApiService;
	private PersonRepository personRepository;
	/*
	public void setExternalApiService(ExternalApiService externalApiService) {
		this.externalApiService = externalApiService;
	}
	*/
	public void setPersonRepository(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public PersonService(PersonRepository personRepository) {
		setPersonRepository(personRepository);
    }
	
	public List<Person> getAllPersons() {
		return personRepository.findAll();
	}
	
	public Person getPerson(int id) {
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
		Person person = personRepository.update(per);
		return person;
	}
	
	public Person deletePerson(Person per) {
		Person perToDelete = personRepository.delete(per);
		return perToDelete;
	}
}
