package repository;

import java.sql.Connection;
import entity.Person;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import util.DatabaseConnection;

	public class PersonRepository {
		
	private Connection connection;
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public PersonRepository(Connection connection) {
		setConnection(connection);
	}

	public List<Person> findAll() {
		List<Person> persons = new ArrayList<>();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM persons ORDER BY name");
			while (rs.next()) {
				Person person = new Person();
				person.setId(rs.getInt("id"));
				person.setName(rs.getString("name"));
				person.setApellido(rs.getString("apellido"));
				person.setFechaNacimiento(rs.getString("fechaNacimiento"));
				persons.add(person);
			}
			if (rs != null) {rs.close();}
			if (stmt != null) {stmt.close();}
			DatabaseConnection.closeConnection();
		} catch (Exception e) {
			throw new RuntimeException("Error fetching persons from database", e);
		}
		return persons;
	}
}
