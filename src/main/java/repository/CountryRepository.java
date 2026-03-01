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
import java.util.Map;
import java.util.HashMap;

public class CountryRepository {

	public List<Country> findAll() {
		List<Country> countries = new ArrayList<>();
		String sql = "SELECT country_id, iso_code, name FROM countries ORDER BY country_id";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Country country = new Country();
				country.setCountryId(rs.getInt("country_id"));
				country.setName(rs.getString("name"));
				country.setIsoCode(rs.getString("iso_code"));
				countries.add(country);
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching countries from database");
		}
		return countries;
	}

	public Country findOne(int id) {
		Country country = null;
		String sql = "SELECT country_id, iso_code, name FROM countries WHERE country_id = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					country = new Country();
					country.setCountryId(rs.getInt("country_id"));
					country.setName(rs.getString("name"));
					country.setIsoCode(rs.getString("iso_code"));
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
		String sql = "SELECT country_id FROM countries WHERE iso_code = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, iso);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					idCountry = rs.getInt("country_id");
					System.out.println("ID del país encontrado: " + idCountry);
				}
			}

		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching country by name");
		}
		return idCountry;
	}

	public Country add(Country c) {
		String sql = "INSERT INTO countries (iso_code, name) VALUES (?, ?)";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, c.getIsoCode());
			stmt.setString(2, c.getName());

			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						c.setCountryId(generatedKeys.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				throw ErrorFactory.duplicate("Country already exists");
			} else {
				throw ErrorFactory.internal("Error adding country to database");
			}
		}

		return c;
	}

	public Country update(Country c) {
		String sql = "UPDATE countries SET name = ? WHERE country_id = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, c.getName());
			stmt.setInt(2, c.getCountryId());

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0) {
				throw ErrorFactory.internal("No country found with ID: " + c.getCountryId());
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				throw ErrorFactory.duplicate("Country already exists");
			} else {
				throw ErrorFactory.internal("Error updating country in database");
			}
		}

		return c;
	}

	public Country delete(Country c) {
		String sql = "DELETE FROM countries WHERE country_id = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, c.getCountryId());
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw ErrorFactory.internal("Error deleting country");
		}
		return c;
	}

	public void saveAll(List<Country> countries) {
		String sql = "INSERT IGNORE INTO countries (iso_code, name) VALUES (?, ?)";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (Country g : countries) {
				stmt.setString(1, g.getIsoCode());
				stmt.setString(2, g.getName());
				stmt.addBatch();
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error saving countries in batch");
		}
	}

	public Map<String, Integer> getMapIds(List<String> isoCodes) {
		if (isoCodes == null || isoCodes.isEmpty()) {
			return new HashMap<>();
		}
		StringBuilder sql = new StringBuilder("SELECT iso_code, country_id FROM countries WHERE iso_code IN (");

		for (int i = 0; i < isoCodes.size(); i++) {
			sql.append(i == 0 ? "?" : ", ?");
		}
		sql.append(")");
		Map<String, Integer> map = new HashMap<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < isoCodes.size(); i++) {
				stmt.setString(i + 1, isoCodes.get(i));
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					map.put(rs.getString("iso_code"), rs.getInt("country_id"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error recuperando mapa de IDs de países");
		}
		return map;
	}

	public void saveBatchRelations(List<Object[]> relations) {
		String sql = "INSERT IGNORE INTO movie_countries (movie_id, country_id) VALUES (?, ?)";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (Object[] row : relations) {
				stmt.setInt(1, (Integer) row[0]);
				stmt.setInt(2, (Integer) row[1]);
				stmt.addBatch();
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error guardando batch de relaciones Película-País");
		}
	}
}
