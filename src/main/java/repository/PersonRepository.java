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
import java.sql.Statement;
import exception.ErrorFactory;
import java.util.Map;
import java.util.HashMap;

public class PersonRepository {

    // Select base para no repetir columnas
    private static final String BASE_SELECT = "SELECT person_id, api_id, name, birthdate, also_known_as, place_of_birth, profile_path FROM persons ";

    public PersonRepository() {
    }

    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY name";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                persons.add(mapResultSetToPerson(rs));
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching persons from database");
        }
        return persons;
    }

    public Person findOne(int id) {
        Person per = null;
        String sql = BASE_SELECT + "WHERE person_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    per = mapResultSetToPerson(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching person by ID from database");
        }
        return per;
    }

    public Person findByApiId(int id_api) {
        Person per = null;
        String sql = BASE_SELECT + "WHERE api_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_api);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    per = mapResultSetToPerson(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching person by API ID from database");
        }
        return per;
    }

    public Person add(Person per) {
        String sql = "INSERT INTO persons (api_id, name, birthdate, also_known_as, place_of_birth, profile_path) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, per.getApiId());
            stmt.setString(2, per.getName());
            stmt.setDate(3, per.getBirthdate());
            stmt.setString(4, per.getAlsoKnownAs());
            stmt.setString(5, per.getPlaceOfBirth());
            stmt.setString(6, per.getProfilePath());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
                    if (keyResultSet.next()) {
                        per.setPersonId(keyResultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("A person with the same API ID already exists.");
            } else {
                throw ErrorFactory.internal("Error adding person to database");
            }
        }
        return per;
    }

    public Person update(Person per) {
        String sql = "UPDATE persons SET api_id = ?, name = ?, birthdate = ?, also_known_as = ?, place_of_birth = ?, profile_path = ? WHERE person_id = ?";

        try (Connection connection = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, per.getApiId());
            stmt.setString(2, per.getName());
            stmt.setDate(3, per.getBirthdate());
            stmt.setString(4, per.getAlsoKnownAs());
            stmt.setString(5, per.getPlaceOfBirth());
            stmt.setString(6, per.getProfilePath());
            stmt.setInt(7, per.getPersonId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("A person with the same API ID already exists.");
            } else {
                throw ErrorFactory.internal("Error updating person in database");
            }
        }
        return per;
    }

    public Person delete(Person per) {
        String sql = "DELETE FROM persons WHERE person_id = ?";

        try (Connection connection = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, per.getPersonId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting person from database");
        }
        return per;
    }

    public void saveAll(List<Person> persons) {
        if (persons == null || persons.isEmpty()) return;
        
        String sql = "INSERT INTO persons (api_id, name, birthdate, also_known_as, place_of_birth, profile_path) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), birthdate = VALUES(birthdate), also_known_as = VALUES(also_known_as), place_of_birth = VALUES(place_of_birth), profile_path = VALUES(profile_path)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            for (Person person : persons) {
                stmt.setInt(1, person.getApiId());
                stmt.setString(2, person.getName());
                stmt.setDate(3, person.getBirthdate());
                stmt.setString(4, person.getAlsoKnownAs());
                stmt.setString(5, person.getPlaceOfBirth());
                stmt.setString(6, person.getProfilePath());

                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving persons to database");
        }
    }

    public List<ActorWithCharacter> findActorsByMovieId(int movieId) {
        List<ActorWithCharacter> actors = new ArrayList<>();
        String sql = "SELECT p.person_id, p.api_id, p.name, p.birthdate, p.also_known_as, p.place_of_birth, p.profile_path, ap.character_name " +
                     "FROM persons p " +
                     "JOIN movie_actors ap ON p.person_id = ap.actor_id " +
                     "WHERE ap.movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Person actor = mapResultSetToPerson(rs);
                    String characterName = rs.getString("character_name");
                    actors.add(new ActorWithCharacter(actor, characterName));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching actors by movie ID from database");
        }
        return actors;
    }

    public List<Person> findDirectorsByMovieId(int movieId) {
        List<Person> directors = new ArrayList<>();
        String sql = "SELECT p.person_id, p.api_id, p.name, p.birthdate, p.also_known_as, p.place_of_birth, p.profile_path " +
                     "FROM persons p " +
                     "JOIN movie_directors dp ON p.person_id = dp.director_id " +
                     "WHERE dp.movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    directors.add(mapResultSetToPerson(rs));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching directors by movie ID from database");
        }
        return directors;
    }

    public void updateAllPersonsbyId_api(List<Person> persons) {
        if (persons == null || persons.isEmpty()) return;
        
        String sql = "UPDATE persons SET birthdate = ?, also_known_as = ?, place_of_birth = ?, profile_path = ? WHERE api_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            for (Person person : persons) {
                stmt.setDate(1, person.getBirthdate());
                stmt.setString(2, person.getAlsoKnownAs());
                stmt.setString(3, person.getPlaceOfBirth());
                stmt.setString(4, person.getProfilePath());
                stmt.setInt(5, person.getApiId());

                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating persons in database");
        }
    }

    public Map<Integer, Integer> getMapIds(List<Integer> apiIds) {
        if (apiIds == null || apiIds.isEmpty()) {
            return new HashMap<>();
        }

        StringBuilder sql = new StringBuilder("SELECT api_id, person_id FROM persons WHERE api_id IN (");
        for (int i = 0; i < apiIds.size(); i++) {
            sql.append(i == 0 ? "?" : ", ?");
        }
        sql.append(")");

        Map<Integer, Integer> map = new HashMap<>();

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
             
            for (int i = 0; i < apiIds.size(); i++) {
                stmt.setInt(i + 1, apiIds.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("api_id"), rs.getInt("person_id"));
                }
            }

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error recuperando mapa de IDs de personas");
        }
        return map;
    }

    // ÚNICO PUNTO DE MAPEO
    private Person mapResultSetToPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setPersonId(rs.getInt("person_id"));
        person.setApiId(rs.getInt("api_id"));
        person.setName(rs.getString("name"));
        person.setAlsoKnownAs(rs.getString("also_known_as"));
        person.setPlaceOfBirth(rs.getString("place_of_birth"));
        person.setBirthdate(rs.getDate("birthdate"));
        person.setProfilePath(rs.getString("profile_path"));
        return person;
    }
}