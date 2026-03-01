package repository;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import entity.Country;
import entity.Movie;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataSourceProvider;
import exception.ErrorFactory;
import java.util.Map;
import java.util.HashMap;

public class MovieRepository {

    public MovieRepository() {

    }

    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT movie_id, api_id, title, synopsis, duration, is_adult, original_title, api_rating, api_votes, popularity, original_language, poster_path, release_year, imdb_id, local_rating_avg, local_reviews_count, release_date FROM movies ORDER BY movie_id";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setMovieId(rs.getInt("movie_id"));
                movie.setApiId(rs.getObject("api_id") != null ? rs.getInt("api_id") : null);
                movie.setTitle(rs.getString("title"));
                movie.setSynopsis(rs.getString("synopsis"));
                movie.setDuration(rs.getTime("duration"));
                movie.setIsAdult(rs.getObject("is_adult") != null ? rs.getBoolean("is_adult") : null);
                movie.setOriginalTitle(rs.getString("original_title"));
                movie.setApiRating(rs.getObject("api_rating") != null ? rs.getDouble("api_rating") : null);
                movie.setOriginalLanguage(rs.getString("original_language"));
                movie.setPosterPath(rs.getString("poster_path"));
                movie.setPopularity(rs.getObject("popularity") != null ? rs.getDouble("popularity") : null);
                movie.setApiVotes(rs.getInt("api_votes"));
                movie.setReleaseYear(rs.getInt("release_year"));
                movie.setImdbId(rs.getString("imdb_id"));

                java.sql.Timestamp releaseDateTs = rs.getTimestamp("release_date");
                if (releaseDateTs != null) {
                    movie.setReleaseDate(releaseDateTs.toLocalDateTime().toLocalDate());
                }

                movies.add(movie);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching movies from database");
        }
        return movies;
    }

    public Movie findOne(int id) {
        Movie movie = null;
        String sql = "SELECT movie_id, api_id, title, synopsis, duration, is_adult, original_title, api_rating, api_votes, popularity, original_language, poster_path, release_year, imdb_id, local_rating_avg, local_reviews_count, release_date FROM movies WHERE movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    movie = mapResultSetToMovie(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching movie with ID " + id + " from database");
        }
        return movie;
    }

    public Movie add(Movie m) {

        String sql = "INSERT INTO movies (api_id, title, synopsis, duration, is_adult, original_title, api_rating, original_language, poster_path, popularity, api_votes, release_year, imdb_id, release_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, m.getApiId());
            stmt.setString(2, m.getTitle());
            stmt.setString(3, m.getSynopsis());
            stmt.setObject(4, m.getDuration());
            stmt.setObject(5, m.getIsAdult());
            stmt.setString(6, m.getOriginalTitle());
            stmt.setObject(7, m.getApiRating());
            stmt.setString(8, m.getOriginalLanguage());
            stmt.setString(9, m.getPosterPath());
            stmt.setObject(10, m.getPopularity());
            stmt.setObject(11, m.getApiVotes());
            stmt.setObject(12, m.getReleaseYear());
            stmt.setString(13, m.getImdbId());

            if (m.getReleaseDate() != null) {
                stmt.setObject(14, m.getReleaseDate());
            } else {
                stmt.setNull(14, java.sql.Types.DATE);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        m.setMovieId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("A movie with the same API ID already exists.");
            } else {
                throw ErrorFactory.internal("Error adding movie to database");
            }
        }
        return m;
    }

    public Movie update(Movie m) {

        String sql = "UPDATE movies SET api_id = ?, title = ?, synopsis = ?, duration = ?, is_adult = ?, original_title = ?, api_rating = ?, original_language = ?, poster_path = ?, popularity = ?, api_votes = ?, release_year = ?, imdb_id = ?, release_date = ? WHERE movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, m.getApiId());
            stmt.setString(2, m.getTitle());
            stmt.setString(3, m.getSynopsis());
            stmt.setObject(4, m.getDuration());
            stmt.setObject(5, m.getIsAdult());
            stmt.setString(6, m.getOriginalTitle());
            stmt.setObject(7, m.getApiRating());
            stmt.setString(8, m.getOriginalLanguage());
            stmt.setString(9, m.getPosterPath());
            stmt.setObject(10, m.getPopularity());
            stmt.setObject(11, m.getApiVotes());
            stmt.setObject(12, m.getReleaseYear());
            stmt.setString(13, m.getImdbId());

            if (m.getReleaseDate() != null) {
                stmt.setObject(14, m.getReleaseDate());
            } else {
                stmt.setNull(14, java.sql.Types.DATE);
            }

            stmt.setInt(15, m.getMovieId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("A movie with the same API ID already exists.");
            } else {
                throw ErrorFactory.internal("Error updating movie in database");
            }
        }
        return m;
    }

    public Movie delete(Movie m) {
        String sql = "DELETE FROM movies WHERE movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, m.getMovieId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting movie from database");
        }
        return m;
    }

    public void saveAll(List<Movie> movies) {
        String sql = "INSERT INTO movies (api_id, title, synopsis, duration, is_adult, original_title, api_rating, original_language, poster_path, popularity, api_votes, release_year, imdb_id, release_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE api_id = VALUES(api_id), title = VALUES(title), popularity = VALUES(popularity), api_votes = VALUES(api_votes), api_rating = VALUES(api_rating), poster_path = VALUES(poster_path), imdb_id = VALUES(imdb_id), synopsis = VALUES(synopsis), release_date = VALUES(release_date)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("Preparando batch insert/update para " + movies.size() + " películas...");
            for (Movie m : movies) {
                stmt.setObject(1, m.getApiId());
                stmt.setString(2, m.getTitle());
                stmt.setString(3, m.getSynopsis());
                stmt.setObject(4, m.getDuration());
                stmt.setObject(5, m.getIsAdult());
                stmt.setString(6, m.getOriginalTitle());
                stmt.setObject(7, m.getApiRating());
                stmt.setString(8, m.getOriginalLanguage());
                stmt.setString(9, m.getPosterPath());
                stmt.setObject(10, m.getPopularity());
                stmt.setObject(11, m.getApiVotes());
                stmt.setObject(12, m.getReleaseYear());
                stmt.setString(13, m.getImdbId());
                if (m.getReleaseDate() != null) {
                    stmt.setObject(14, m.getReleaseDate());
                } else {
                    stmt.setNull(14, java.sql.Types.TIMESTAMP);
                }

                stmt.addBatch();
            }
            stmt.executeBatch();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving movies to database");
        }
    }

    public void updateMovieGenres(int movieId, List<Integer> genres) {
        String sql = "INSERT INTO movie_genres (movie_id, genre_id) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer genreId : genres) {
                stmt.setInt(1, movieId);
                stmt.setInt(2, genreId);
                stmt.addBatch();
            }
            stmt.executeBatch();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating movie genres in database");
        }

    }

    public float getMovieRating(String movieId){
        try {

            System.out.println("Rating para la pelicula con  en el getMovieRating id: " + movieId );
            URL url = new URL("https://api.imdbapi.dev/titles/" + movieId );
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader.readLine(), JsonObject.class);
            reader.close();
            conn.disconnect();
            if (json.has("rating") && !json.get("rating").isJsonNull()) {
                JsonObject ratingObj = json.getAsJsonObject("rating");
                return ratingObj.get("aggregateRating").getAsFloat();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al obtener rating de película " + movieId, e);
        }
        return 0.0f;
    }

    public void updateMovieActors(List<Object[]> relations) {
        String sql = "INSERT IGNORE INTO movie_actors (movie_id, actor_id, character_name) VALUES (?, ?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Object[] row : relations) {
                stmt.setInt(1, (Integer) row[0]);
                stmt.setInt(2, (Integer) row[1]);
                stmt.setString(3, (String) row[2]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving batch movie actors");
        }
    }

    public void updateMovieDirectors(List<Object[]> relations) {
        String sql = "INSERT IGNORE INTO movie_directors (movie_id, director_id) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Object[] row : relations) {
                stmt.setInt(1, (Integer) row[0]);
                stmt.setInt(2, (Integer) row[1]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving batch movie directors");
        }
    }

    public List<Movie> findByName(String searchTerm) {
        List<Movie> movies = new ArrayList<>();

        String sql = "SELECT DISTINCT p.movie_id, p.api_id, p.title, p.synopsis, p.duration, p.is_adult, p.original_title, p.api_rating, p.original_language, p.poster_path, p.popularity, p.api_votes, p.release_year, p.imdb_id, p.local_rating_avg, p.local_reviews_count, p.release_date " +
                     "FROM movies p " +
                     "LEFT JOIN movie_actors ap ON p.movie_id = ap.movie_id " +
                     "LEFT JOIN movie_directors dp ON p.movie_id = dp.movie_id " +
                     "LEFT JOIN persons per_actor ON ap.actor_id = per_actor.person_id " +
                     "LEFT JOIN persons per_director ON dp.director_id = per_director.person_id " +
                     "WHERE p.title LIKE ? OR p.original_title LIKE ? OR per_actor.name LIKE ? OR per_director.name LIKE ? " +
                     "ORDER BY p.popularity DESC LIMIT 20";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error searching movies by name");
        }
        return movies;
    }

    public List<Movie> findMostPopular(int limit) {

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT movie_id, api_id, title, synopsis, duration, is_adult, original_title, api_rating, api_votes, popularity, original_language, poster_path, release_year, imdb_id, local_rating_avg, local_reviews_count, release_date FROM movies ORDER BY popularity DESC LIMIT ?")) {

            stmt.setInt(1, limit);
            List<Movie> movies = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
            return movies;
        } catch (Exception e) {
            return createMockMovies(limit);
        }
    }

    public List<Movie> findTopRated(int limit) {
        List<Movie> movies = new ArrayList<>();

        String sql = "SELECT movie_id, api_id, title, synopsis, duration, is_adult, original_title, api_rating, api_votes, popularity, original_language, poster_path, release_year, imdb_id, local_rating_avg, local_reviews_count, release_date FROM movies WHERE api_rating > 7.0 ORDER BY api_rating DESC LIMIT ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching top rated movies");
        }
        return movies;
    }

    public List<Movie> findRecentMovies(int limit) {
        List<Movie> movies = new ArrayList<>();

        String sql = "SELECT movie_id, api_id, title, synopsis, duration, is_adult, original_title, api_rating, api_votes, popularity, original_language, poster_path, release_year, imdb_id, local_rating_avg, local_reviews_count, release_date FROM movies WHERE release_year >= 2020 ORDER BY release_year DESC, popularity DESC LIMIT ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching recent movies");
        }
        return movies;
    }

    public List<Movie> findRandom(int limit) {
        try {
            return findRandomWithRand(limit);
        } catch (Exception e) {
            System.err.println("Database unavailable, returning mock data: " + e.getMessage());
            return createMockMovies(limit);
        }
    }

    private List<Movie> createMockMovies(int limit) {
        List<Movie> movies = new ArrayList<>();
        String[] titles = {"The Shawshank Redemption", "The Godfather", "The Dark Knight", "Pulp Fiction", "Forrest Gump", "Inception", "The Matrix", "Goodfellas", "The Lord of the Rings", "Star Wars"};
        for (int i = 0; i < Math.min(limit, titles.length); i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + 1);
            movie.setTitle(titles[i]);
            movie.setSynopsis("This is a sample movie description for " + titles[i] + ".");
            movie.setPosterPath("/images/placeholder.jpg");
            movie.setApiRating(7.5 + (i % 3));
            movie.setReleaseYear(1990 + (i * 3));
            movies.add(movie);
        }
        return movies;
    }

    private List<Movie> findRandomWithRand(int limit) throws SQLException {
        List<Movie> movies = new ArrayList<>();

        String sql = "SELECT movie_id, api_id, title, synopsis, duration, is_adult, original_title, api_rating, api_votes, popularity, original_language, poster_path, release_year, imdb_id, local_rating_avg, local_reviews_count, release_date FROM movies ORDER BY RAND() LIMIT ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in findRandomWithRand: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            throw e;
        }
        return movies;
    }

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setMovieId(rs.getInt("movie_id"));
        movie.setApiId(rs.getObject("api_id") != null ? rs.getInt("api_id") : null);
        movie.setTitle(rs.getString("title"));
        movie.setSynopsis(rs.getString("synopsis"));
        movie.setDuration(rs.getTime("duration"));
        movie.setIsAdult(rs.getObject("is_adult") != null ? rs.getBoolean("is_adult") : null);
        movie.setOriginalTitle(rs.getString("original_title"));
        movie.setApiRating(rs.getObject("api_rating") != null ? rs.getDouble("api_rating") : null);
        movie.setOriginalLanguage(rs.getString("original_language"));
        movie.setPosterPath(rs.getString("poster_path"));
        movie.setPopularity(rs.getObject("popularity") != null ? rs.getDouble("popularity") : null);
        movie.setApiVotes(rs.getInt("api_votes"));
        movie.setReleaseYear(rs.getInt("release_year"));
        movie.setImdbId(rs.getString("imdb_id"));
        movie.setLocalRatingAvg(rs.getObject("local_rating_avg") != null ? rs.getDouble("local_rating_avg") : 0.0);
        movie.setLocalReviewsCount(rs.getObject("local_reviews_count") != null ? rs.getInt("local_reviews_count") : 0);

        String releaseDateStr = rs.getString("release_date");
        if (releaseDateStr != null && releaseDateStr.length() >= 10) {
            movie.setReleaseDate(java.time.LocalDate.parse(releaseDateStr.substring(0, 10)));
        } else {
            movie.setReleaseDate(null);
        }

        return movie;
    }

    public List<Movie> movieFilter(String nombre, String genero, int desde, int hasta) {
        System.out.println("Filtrando películas - Nombre: " + nombre + ", Género: " + genero + ", Años: " + desde + "-" + hasta);

        try {
            List<Movie> movies = new ArrayList<>();
            StringBuilder sql = new StringBuilder();


            sql.append("SELECT DISTINCT p.movie_id, p.api_id, p.title, p.synopsis, p.duration, ")
               .append("p.is_adult, p.original_title, p.api_rating, p.original_language, ")
               .append("p.poster_path, p.popularity, p.api_votes, p.release_year, p.imdb_id, ")
               .append("p.local_rating_avg, p.local_reviews_count, p.release_date ")
               .append("FROM movies p ");

            if (genero != null && !genero.isBlank()) {
                sql.append("INNER JOIN movie_genres gp ON gp.movie_id = p.movie_id ")
                   .append("INNER JOIN genres g ON g.genre_id = gp.genre_id ");
            }

            List<String> conditions = new ArrayList<>();
            if (nombre != null && !nombre.trim().isEmpty()) {
                conditions.add("(p.title LIKE ? OR p.original_title LIKE ?)");
            }
            if (genero != null && !genero.isBlank()) {
                conditions.add("g.name = ?");
            }
            if (desde != 0 && hasta != 0) {
                conditions.add("p.release_year BETWEEN ? AND ?");
            } else if (desde == 0 && hasta != 0) {
                conditions.add("p.release_year <= ?");
            } else if (desde != 0 && hasta == 0) {
                conditions.add("p.release_year >= ?");
            }

            if (!conditions.isEmpty()) {
                sql.append("WHERE ").append(String.join(" AND ", conditions));
            }

            try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

                int paramIndex = 1;
                if (nombre != null && !nombre.trim().isEmpty()) {
                    String searchPattern = "%" + nombre.trim() + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }
                if (genero != null && !genero.isBlank()) {
                    stmt.setString(paramIndex++, genero);
                }
                if (desde != 0 && hasta != 0) {
                    stmt.setInt(paramIndex++, desde);
                    stmt.setInt(paramIndex++, hasta);
                } else if (desde == 0 && hasta != 0) {
                    stmt.setInt(paramIndex++, hasta);
                } else if (desde != 0 && hasta == 0) {
                    stmt.setInt(paramIndex++, desde);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Película encontrada: " + rs.getString("title") + " Año: " + rs.getInt("release_year"));
                        movies.add(mapResultSetToMovie(rs));
                    }
                }
            }

            System.out.println("Se encontraron " + movies.size() + " películas");
            return movies;

        } catch (Exception e) {
            System.out.println("Error during movie filtering: " + nombre + genero + desde + hasta);
            System.err.println("Error filtering movies, returning mock data: " + e.getMessage());
            e.printStackTrace();
            return createMockMovies(10);
        }
    }

    public void updateReviewStats(int movieId) {
        String sql = "UPDATE movies SET local_rating_avg = (SELECT COALESCE(AVG(rating), 0) FROM reviews WHERE movie_id = ?), local_reviews_count = (SELECT COUNT(*) FROM reviews WHERE movie_id = ?) WHERE movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, movieId);
            stmt.setInt(3, movieId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating movie review stats");
        }
    }

    public List<Country> getCountriesByMovieId(int movieId) {
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT c.country_id, c.iso_code, c.name " +
                     "FROM countries c " +
                     "INNER JOIN movie_countries pp ON c.country_id = pp.country_id " +
                     "WHERE pp.movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Country country = new Country();
                    country.setCountryId(rs.getInt("country_id"));
                    country.setIsoCode(rs.getString("iso_code"));
                    country.setName(rs.getString("name"));
                    countries.add(country);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching countries for movie");
        }
        return countries;
    }

    public Map<Integer, Integer> findAllByApiIds(List<Integer> apiIds) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT api_id, movie_id FROM movies WHERE api_id IN (");

        for (int i = 0; i < apiIds.size(); i++) {
            sqlBuilder.append("?");
            if (i < apiIds.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");
        Map<Integer, Integer> idMap = new HashMap<>();

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < apiIds.size(); i++) {
                stmt.setInt(i + 1, apiIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int apiId = rs.getInt("api_id");
                    int dbId = rs.getInt("movie_id");
                    idMap.put(apiId, dbId);
                }
            }

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error mapping API IDs to Database IDs");
        }
        return idMap;
    }

    public void saveAllMovieGenres(List<Object[]> relacionesMovieGenre) {
        String sql = "INSERT IGNORE INTO movie_genres (movie_id, genre_id) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Object[] fila : relacionesMovieGenre) {
                stmt.setObject(1, fila[0]);
                stmt.setObject(2, fila[1]);
                stmt.addBatch();
            }
            System.out.println("Guardando " + relacionesMovieGenre.size() + " relaciones película-género...");
            stmt.executeBatch();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving bulk movie genres relations");
        }
    }

    public void updateBatch(List<Movie> movies) {
        String sql = "UPDATE movies SET duration = ?, imdb_id = ? WHERE movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Movie movie : movies) {
                stmt.setObject(1, movie.getDuration());
                stmt.setString(2, movie.getImdbId());
                stmt.setInt(3, movie.getMovieId());

                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error actualizando lote de películas");
        }
    }

}
