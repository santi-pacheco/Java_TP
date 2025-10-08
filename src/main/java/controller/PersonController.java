package controller;

import repository.PersonRepository;
import service.PersonService;
import entity.Person;
import java.util.List;

public class PersonController {

	private PersonService personService;

	public void setPersonService(PersonService personService) {
			this.personService = personService;
		}

	public PersonController() {
            PersonRepository personRepository = new PersonRepository();
            this.personService = new PersonService(personRepository);
    }
	
	public List<Person> getPersons() {
		try {
			List<Person> persons = personService.getAllPersons();
			return persons;
		} catch (Exception e) {
			throw new RuntimeException("Error getting persons", e);
		}
	}
	
	public Person getPersonById(int id) {
		try {
			Person personById = personService.getPerson(id);
			return personById;
		} catch (Exception e) {
			throw new RuntimeException("Error getting person by ID", e);
		}	
	}
	
	public boolean addPerson(Person per) {
		try {
			Person createdPerson = personService.createPerson(per);
			if (createdPerson != null) {
	            return true;
	        } else {
	            return false;
	        }
		} catch (Exception e) {
			throw new RuntimeException("Error adding person", e);
		}
	}
	
	public boolean modifyPerson(Person per) {
		try {
			Person createdPerson = personService.updatePerson(per);
			if (createdPerson != null) {
	            return true;
	        } else {
	            return false;
	        }
		} catch (Exception e) {
			throw new RuntimeException("Error modifying person", e);
		}
	}
	
	public Person removePerson(Person per) {
		try {
			Person deleted = personService.deletePerson(per);
			return deleted;
		} catch (Exception e) {
			throw new RuntimeException("Error removing person", e);
		}
	}
}