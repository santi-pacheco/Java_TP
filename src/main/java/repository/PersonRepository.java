package repository;

import java.sql.Connection;
import entity.Person;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import util.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

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
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM persons ORDER BY name");
			while (rs.next()) {
				Person person = new Person();
				person.setId(rs.getInt("id"));
				person.setName(rs.getString("name"));
				person.setApellido(rs.getString("apellido"));
				person.setFechaNacimiento(convertToLocalDate(rs.getDate("fechaNacimiento")));
				persons.add(person);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching persons from database", e);
		} finally {
			try {
				if (stmt != null) {stmt.close();}
				if (rs != null) {rs.close();}
				DatabaseConnection.closeConnection();
			} catch (SQLException e) {
				throw new RuntimeException("Error closing resources after deleting person", e);
			}
		}
		return persons;
	}
	
	public Person findOne(int id) {
		Person per = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement("SELECT * FROM persons WHERE id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				per = new Person();
				per.setId(rs.getInt("id"));
				per.setName(rs.getString("name"));
				per.setApellido(rs.getString("apellido"));
				per.setFechaNacimiento(convertToLocalDate(rs.getDate("fechaNacimiento")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching person by ID from database", e);
		} finally {
			try {
				if (stmt != null) {stmt.close();}
				if (rs != null) {rs.close();}
				DatabaseConnection.closeConnection();
			} catch (SQLException e) {
				throw new RuntimeException("Error closing resources after deleting person", e);
			}
		}
		return per;
	}
	
	public Person add(Person per) {
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("INSERT INTO persons (name, apellido, fechaNacimiento) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			stmt.setString(1, per.getName());
			stmt.setString(2, per.getApellido());
			stmt.setDate(3, convertToDate(per.getFechaNacimiento()));
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
		} finally {
			try {
				if (stmt != null) {stmt.close();}
				DatabaseConnection.closeConnection();
			} catch (SQLException e) {
				throw new RuntimeException("Error closing resources after deleting person", e);
			}
		}
		return per;
	}
	
	public Person update(Person per) {
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("UPDATE persons SET name = ?, apellido = ?, fechaNacimiento = ? WHERE id = ?");
			stmt.setString(1, per.getName());
			stmt.setString(2, per.getApellido());
			stmt.setDate(3, convertToDate(per.getFechaNacimiento()));
			stmt.setInt(4, per.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error preparing update statement for person", e);
		} finally {
			try {
				if (stmt != null) {stmt.close();}
				DatabaseConnection.closeConnection();
			} catch (SQLException e) {
				throw new RuntimeException("Error closing resources after preparing update statement", e);
			}
		}
		return per;
	}
	
	public Person delete(Person per) {
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("DELETE FROM persons WHERE id = ?");
			stmt.setInt(1, per.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error deleting person from database", e);
		} finally {
			try {
				if (stmt != null) {stmt.close();}
				DatabaseConnection.closeConnection();
			} catch (SQLException e) {
				throw new RuntimeException("Error closing resources after deleting person", e);
			}
		}
		return per;
	}
	
	public List<Person> saveAll(List<Person> persons) {
		for (Person per : persons) {
			add(per);
		}
		return persons;
	}
	
	public Date convertToDate(LocalDate dateLocal) {
		if (dateLocal != null) {
			return Date.valueOf(dateLocal);
		} else {
			return null;
		}
	}
	
	public LocalDate convertToLocalDate(Date dateSql) {
		if (dateSql != null) {
			return dateSql.toLocalDate();
		} else {
			return null;
		}
	}
	
}
