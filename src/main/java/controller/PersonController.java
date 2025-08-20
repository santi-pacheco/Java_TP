package controller;

import java.sql.Connection;
import java.sql.SQLException;

import repository.PersonRepository;
import service.ExternalApiService;
import service.PersonService;
import util.DatabaseConnection;
import entity.Person;
import java.util.List;

public class PersonController {

	private PersonService personService;

	public void setPersonService(PersonService personService) {
			this.personService = personService;
		}

	public PersonController(String tmdbApiKey) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            PersonRepository personRepository = new PersonRepository(connection);
            ExternalApiService externalApiService = new ExternalApiService(tmdbApiKey);
            setPersonService(new PersonService(externalApiService, personRepository));
        } catch (SQLException e) {	
            throw new RuntimeException("Error initializing database connection", e);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing PersonController", e);
        }
    }
	
	public List<Person> getPersons() {
		try {
			List<Person> persons = personService.getAllPersons();
			return persons;
		} catch (Exception e) {
			throw new RuntimeException("Error getting persons", e);
		}
	}
}