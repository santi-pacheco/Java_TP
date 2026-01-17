package controller;

import service.PersonService;
import entity.Person;
import java.util.List;
import java.util.Map;

public class PersonController {

	private PersonService personService;

	public PersonController(PersonService personService) {
            this.personService = personService;
    }
	
	public List<Person> getPersons() {
		List<Person> persons = personService.getAllPersons();
		return persons;
	}
	
	public Person getPersonById(int id) {
		Person personById = personService.getPersonById(id);
		return personById;
	}
	
	public Person createPerson(Person per) {
		Person createdPerson = personService.createPerson(per);
		return createdPerson;
	}
	
	public Person modifyPerson(Person per) {
		Person createdPerson = personService.updatePerson(per);
		return createdPerson;
	}
	
	public void removePerson(Person per) {
		personService.deletePerson(per);
	}
	
	public List<util.DiscoverReflectionMain.actorCharacter> saveActors(List <service.ExternalApiService.PersonWithCharacter> personWithCharacter){
		return personService.saveActors(personWithCharacter);
	}
	
	public List<Person> saveDirectors(List<Person> director) {
		return personService.saveDirectors(director);
	}
	
	public void updateAllPersonsbyId_api(List<Person> persons) { 
		personService.updateAllPersonsbyId_api(persons);
	}
	
	public void saveAllPersons(List<Person> persons) {
		personService.saveAllPersons(persons);
	}
	
	public Map<Integer, Integer> getMapIds(List<Integer> apiIds) {
		return personService.getMapIds(apiIds);
	}	
}