package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import entity.Country;
import exception.ErrorFactory;
import util.DataSourceProvider;

public class CountryRepository {

    private static final String BASE_SELECT = "SELECT country_id, iso_code, name FROM countries";

    public List<Country> findAll() {
        List<Country> countries = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY country_id";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                countries.add(mapResultSetToCountry(rs));
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching countries from database");
        }
        return countries;
    }

    public Country findOne(int id) {
        Country country = null;
        String sql = BASE_SELECT + " WHERE country_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    country = mapResultSetToCountry(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching country by ID");
        }
        return country;
    }

    public int findOneByISO(String iso) {
        int idCountry = -1;
        String sql = "SELECT country_id FROM countries WHERE iso_code = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, iso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idCountry = rs.getInt("country_id");
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching country by ISO code");
        }
        return idCountry;
    }

    public Country add(Country c) {
        String sql = "INSERT INTO countries (iso_code, name) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
                throw ErrorFactory.duplicate("El país ya existe en la base de datos.");
            }
            throw ErrorFactory.internal("Error adding country to database");
        }
        return c;
    }

    public Country update(Country c) {
        String sql = "UPDATE countries SET name = ?, iso_code = ? WHERE country_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getName());
            stmt.setString(2, c.getIsoCode());
            stmt.setInt(3, c.getCountryId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("El código ISO ya está en uso por otro país.");
            }
            throw ErrorFactory.internal("Error updating country in database");
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
        if (countries == null || countries.isEmpty()) return;
        
        String sql = "INSERT IGNORE INTO countries (iso_code, name) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            for (Country c : countries) {
                stmt.setString(1, c.getIsoCode());
                stmt.setString(2, c.getName());
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
        if (relations == null || relations.isEmpty()) return;
        
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

    // ÚNICO PUNTO DE MAPEO
    private Country mapResultSetToCountry(ResultSet rs) throws SQLException {
        Country country = new Country();
        country.setCountryId(rs.getInt("country_id"));
        country.setIsoCode(rs.getString("iso_code"));
        country.setName(rs.getString("name"));
        return country;
    }
}