package service;

import entity.Person;
import repository.PersonRepository;
import info.movito.themoviedbapi.tools.TmdbException;
import java.util.List;

public class PersonService {
	
	private ExternalApiService externalApiService;
	private PersonRepository personRepository;
	
	public void setExternalApiService(ExternalApiService externalApiService) {
		this.externalApiService = externalApiService;
	}

	public void setPersonRepository(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public PersonService(ExternalApiService externalApiService, PersonRepository personRepository) {
		setExternalApiService(externalApiService);
		setPersonRepository(personRepository);
    }
	
	public List<Person> getAllPersons() {
		List<Person> localPersons = personRepository.findAll();
		if (localPersons.isEmpty()) {
			// If local list is empty, fetch from external API
			try {
				List<info.movito.themoviedbapi.model.people.Person> tmdbPersons = 
					externalApiService.getMoviePersons();
				localPersons = externalApiService.convertToLocalPersons(tmdbPersons);
				
				// Save to database for future use
				personRepository.saveAll(localPersons);
				
			} catch (Exception e) {
				throw new RuntimeException("Error fetching persons from external API", e);
			}
		}
		return localPersons;
	}
	
	public Person getPerson(int id) {
		Person person = personRepository.findOne(id);
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
		Person perToDelete = personRepository.delete(per));
		return perToDelete;
	}
}
