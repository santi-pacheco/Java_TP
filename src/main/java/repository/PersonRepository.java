package repository;

import java.sql.Connection;

import entity.Person;
import entity.ActorWithCharacter;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import util.DataSourceProvider;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import exception.ErrorFactory;

	public class PersonRepository {
		
	//private Connection connection;
	
		/*
		 id_persona
		 id_api
		 name
		 birthdate
		 also_known_as
		 place_of_birth
		*/
		
	public PersonRepository() {
		//Ya no se crea la conexión aquí, se obtiene en cada método usando el pool de conexiones
	}

	public List<Person> findAll() {
		List<Person> persons = new ArrayList<>();
		String sql = "SELECT id_persona, id_api, name, birthdate, also_known_as, place_of_birth  FROM personas ORDER BY name";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				Person person = new Person();
				person.setId(rs.getInt("id_persona"));
				person.setId_api(rs.getInt("id_api"));
				person.setName(rs.getString("name"));
				person.setAlso_known_as(rs.getString("also_known_as"));
				person.setPlace_of_birth(rs.getString("place_of_birth"));
				person.setBirthDate(rs.getDate("birthdate"));
				persons.add(person);
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching persons from database");
		}
		return persons;
	}
	
	public Person findOne(int id) {
		Person per = null;
		String sql = "SELECT id_persona, id_api, name, birthdate, also_known_as, place_of_birth FROM personas WHERE id_persona = ?";
		
		try ( Connection conn = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					per = new Person();
					per.setId(rs.getInt("id_persona"));
					per.setId_api(rs.getInt("id_api"));
					per.setName(rs.getString("name"));
					per.setAlso_known_as(rs.getString("also_known_as"));
					per.setPlace_of_birth(rs.getString("place_of_birth"));
					per.setBirthDate(rs.getDate("birthdate"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching person by ID from database");
		}
		return per;
	}
	
	public Person add(Person per) {
		String sql = "INSERT INTO personas (id_api, name, birthdate, also_known_as, place_of_birth) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			
			stmt.setInt(1, per.getId_api());
			stmt.setString(2, per.getName());
			stmt.setDate(3, per.getBirthDate());
			stmt.setString(4, per.getAlso_known_as());
			stmt.setString(5, per.getPlace_of_birth());
			
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
					if (keyResultSet.next()) {
						per.setId(keyResultSet.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) { // Código de error para clave duplicada en MySQL
				throw ErrorFactory.duplicate("A person with the same API ID already exists.");
			} else {
				throw ErrorFactory.internal("Error adding person to database");
			}
		}
		return per;
	}
	
	public Person update(Person per) {
		String sql = "UPDATE personas SET id_api = ?, name = ?, birthdate = ?, also_known_as = ?, place_of_birth = ? WHERE id_persona = ?";
		
		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql)) {
			
			stmt.setInt(1, per.getId_api());
			stmt.setString(2, per.getName());
			stmt.setDate(3, per.getBirthDate());
			stmt.setString(4, per.getAlso_known_as());
			stmt.setString(5, per.getPlace_of_birth());
			stmt.setInt(6, per.getId());
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) { // Código de error para clave duplicada en MySQL
				throw ErrorFactory.duplicate("A person with the same API ID already exists.");
			} else {
				throw ErrorFactory.internal("Error updating person in database");
			}
		}
		return per;
	}
	
	public Person delete(Person per) {
		String sql = "DELETE FROM personas WHERE id_persona = ?";
		
		try ( Connection connection = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql)) {
			
			stmt.setInt(1, per.getId());
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error deleting person from database");
		}
		return per;
	}
	
	public void saveAll(List<Person> persons) {
        String sql = "INSERT INTO personas (id_persona, id_api, name, birthdate, also_known_as, place_of_birth) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id_persona = VALUES(id_persona), id_api = VALUES(id_api), name = VALUES(name), birthdate = VALUES(birthdate, also_known_as = VALUES(also_known_as), place_of_birth = VALUES(place_of_birth)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
       	     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (Person person : persons) {
            	stmt.setInt(1, person.getId());
            	stmt.setInt(2, person.getId_api());
    			stmt.setString(3, person.getName());
    			stmt.setDate(4, person.getBirthDate());
    			stmt.setString(5, person.getAlso_known_as());
    			stmt.setString(6, person.getPlace_of_birth());
    			
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving genres to database");
        }
    }

	public Person findByApiId(int id_api) {
		Person per = null;
		String sql = "SELECT id_persona, id_api, name, birthdate, also_known_as, place_of_birth FROM personas WHERE id_api = ?";
		
		try ( Connection conn = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, id_api);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					per = new Person();
					per.setId(rs.getInt("id_persona"));
					per.setId_api(rs.getInt("id_api"));
					per.setName(rs.getString("name"));
					per.setAlso_known_as(rs.getString("also_known_as"));
					per.setPlace_of_birth(rs.getString("place_of_birth"));
					per.setBirthDate(rs.getDate("birthdate"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching person by API ID from database");
		}
		return per;
	}
	
	public List<ActorWithCharacter> findActorsByMovieId(int movieId) {
		List<ActorWithCharacter> actors = new ArrayList<>();
		String sql = "SELECT p.id_persona, p.id_api, p.name, p.birthdate, p.also_known_as, p.place_of_birth, ap.character_name " +
					 "FROM personas p " +
					 "JOIN actores_peliculas ap ON p.id_persona = ap.id_persona " +
					 "WHERE ap.id_pelicula = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, movieId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Person actor = new Person();
					actor.setId(rs.getInt("id_persona"));
					actor.setId_api(rs.getInt("id_api"));
					actor.setName(rs.getString("name"));
					actor.setAlso_known_as(rs.getString("also_known_as"));
					actor.setPlace_of_birth(rs.getString("place_of_birth"));
					actor.setBirthDate(rs.getDate("birthdate"));
					
					String characterName = rs.getString("character_name");
					actors.add(new ActorWithCharacter(actor, characterName));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching actors: " + e.getMessage());
			// Return empty list instead of throwing exception
			return new ArrayList<>();
		}
		return actors;
	}
	
	public List<Person> findDirectorsByMovieId(int movieId) {
		List<Person> directors = new ArrayList<>();
		String sql = "SELECT p.id_persona, p.id_api, p.name, p.birthdate, p.also_known_as, p.place_of_birth " +
					 "FROM personas p " +
					 "JOIN directores_peliculas dp ON p.id_persona = dp.id_persona " +
					 "WHERE dp.id_pelicula = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, movieId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Person director = new Person();
					director.setId(rs.getInt("id_persona"));
					director.setId_api(rs.getInt("id_api"));
					director.setName(rs.getString("name"));
					director.setAlso_known_as(rs.getString("also_known_as"));
					director.setPlace_of_birth(rs.getString("place_of_birth"));
					director.setBirthDate(rs.getDate("birthdate"));
					directors.add(director);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching directors: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
		return directors;
	}	
}
