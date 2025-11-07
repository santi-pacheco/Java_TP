package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Country;
import exception.ErrorFactory;
import util.DataSourceProvider;

public class CountryRepository {
	
	public List<Country> findAll() {
		List<Country> countries = new ArrayList<>(); 
		String sql = "SELECT id_country, name, iso_country FROM paises ORDER BY id_country";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				Country country = new Country();
				country.setId(rs.getInt("id_country"));
				country.setEnglish_name(rs.getString("name"));
				country.setIso_3166_1(rs.getString("iso_country"));
				countries.add(country);
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching countries from database");
		}
		return countries;
	}
	
	public Country findOne(int id) {
		Country country = null;
		String sql = "SELECT id_country, name, iso_country FROM paises WHERE id_country = ?";
		
		try ( Connection conn = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					country = new Country();
					country.setId(rs.getInt("id_country"));
					country.setEnglish_name(rs.getString("name"));
					country.setIso_3166_1(rs.getString("iso_country"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching country by ID");
		}
		
		return country;
	}
	
	public int findOneByISO(String iso) {
		System.out.println("Buscando país por código ISO: " + iso);
		int idCountry = -1;
		String sql = "SELECT id_country FROM paises WHERE iso_country = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			
			stmt.setString(1, iso);
			

			
		
			try (ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
					idCountry = rs.getInt("id_country");
					System.out.println("ID del país encontrado: " + idCountry);
				}
			}
	
		}
		catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching country by name");
		}
		return idCountry;
	}
	
	public Country add(Country c) {
		String sql = "INSERT INTO paises (iso_country, name) VALUES (?, ?)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			
			stmt.setString(1, c.getIso_3166_1());
			stmt.setString(2, c.getEnglish_name());
			
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						c.setId(generatedKeys.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) { // Unique violation
				throw ErrorFactory.duplicate("Country already exists");
			} else {
				throw ErrorFactory.internal("Error adding country to database");
			}
		}
		
		return c;
	}
	
	public Country update(Country c) {
		String sql = "UPDATE paises SET name = ? WHERE id_country = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setString(1, c.getEnglish_name());
			stmt.setInt(2, c.getId());
			
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0) {
				throw ErrorFactory.internal("No country found with ID: " + c.getId());
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) { // Unique violation
				throw ErrorFactory.duplicate("Country already exists");
			} else {
				throw ErrorFactory.internal("Error updating country in database");
			}
		}
		
		return c;
	}
	
	public Country delete(Country c) {
		String sql = "DELETE FROM paises WHERE id_country = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, c.getId());
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw ErrorFactory.internal("Error deleting country");
		}
		return c;
	}
	
	public void saveAll(List<Country> countries) { 
		System.out.println("Creando tabla countries..." + countries.size());
		String sql = "INSERT INTO paises (iso_country, name) VALUES (?, ?)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			System.out.println("Preparando batch de países...");
			for (Country g : countries) {
				stmt.setString(1, g.getIso_3166_1());
				stmt.setString(2, g.getEnglish_name());
				stmt.addBatch();
				System.out.println("Agregado a batch: " + g.getEnglish_name());
			}
			
			stmt.executeBatch();
			System.out.println("Países guardados en batch correctamente.");
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error saving countries in batch");
		}
	}
	
	
	public void saveCountryMovie(int countryId, int MovieId) {
		System.out.println("Guardando asociación país-película: " + countryId + " - " + MovieId);
		String sql = "INSERT INTO peliculas_paises (id_country, id_pelicula) VALUES (?, ?)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, countryId);
			stmt.setInt(2, MovieId);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error saving country-movie association" + e.getMessage());
		}
		
		
	}
	
	
	
}