package repository;

import java.sql.Connection;

import entity.Person;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import util.DataSourceProvider;
import java.sql.PreparedStatement;
import java.sql.SQLException;

	public class PersonRepository {
		
	//private Connection connection;
	
	public PersonRepository() {
		//Ya no se crea la conexión aquí, se obtiene en cada método usando el pool de conexiones
	}

	public List<Person> findAll() {
		List<Person> persons = new ArrayList<>();
		String sql = "SELECT id_persona, name, apellido, birthdate FROM personas ORDER BY name";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				Person person = new Person();
				person.setId(rs.getInt("id_persona"));
				person.setName(rs.getString("name"));
				person.setApellido(rs.getString("apellido"));
				person.setBirthDate(rs.getDate("birthdate"));
				persons.add(person);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching persons from database", e);
		}
		return persons;
	}
	
	public Person findOne(int id) {
		Person per = null;
		String sql = "SELECT id_persona, name, apellido, birthdate FROM personas WHERE id_persona = ?";
		
		try ( Connection conn = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					per = new Person();
					per.setId(rs.getInt("id_persona"));
					per.setName(rs.getString("name"));
					per.setApellido(rs.getString("apellido"));
					per.setBirthDate(rs.getDate("birthdate"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching person by ID from database", e);
		}
		return per;
	}
	
	public Person add(Person per) {
		String sql = "INSERT INTO personas (name, apellido, birthdate) VALUES (?, ?, ?)";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			
			stmt.setString(1, per.getName());
			stmt.setString(2, per.getApellido());
			stmt.setDate(3, per.getBirthDate());
			
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
					if (keyResultSet.next()) {
						per.setId(keyResultSet.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error adding person to database", e);
		}
		return per;
	}
	
	public Person update(Person per) {
		String sql = "UPDATE personas SET name = ?, apellido = ?, birthdate = ? WHERE id_persona = ?";
		
		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql)) {
			
			stmt.setString(1, per.getName());
			stmt.setString(2, per.getApellido());
			stmt.setDate(3, per.getBirthDate());
			stmt.setInt(4, per.getId());
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new RuntimeException("Error preparing update statement for person", e);
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
			throw new RuntimeException("Error deleting person from database", e);
		}
		return per;
	}
	
	public void saveAll(List<Person> persons) {
        String sql = "INSERT INTO personas (id_persona, name, apellido, birthdate) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), apellido = VALUES(apellido), birthdate = VALUES(birthdate)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
       	     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (Person person : persons) {
                stmt.setInt(1, person.getId());
                stmt.setString(2, person.getName());
                stmt.setString(3, person.getApellido());
                stmt.setDate(4, person.getBirthDate());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving genres to database", e);
        }
    }
		
}
