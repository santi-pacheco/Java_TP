package repository;

import entity.Watchlist;
import exception.ErrorFactory;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataSourceProvider;

public class WatchlistRepository {

    public WatchlistRepository() {
        
    }

    public Watchlist findAll(int userId) {
        Watchlist wl = new Watchlist();
        String sql = "SELECT * FROM watchlists WHERE user_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wl.setWatchlistId(rs.getInt("watchlist_id"));
                    wl.setName(rs.getString("name"));
                    wl.setUserId(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching watchlists from database");
        }
        return wl;
    }

    public Watchlist findOne(int userId) {
        Watchlist wl = new Watchlist();
        String sql = "SELECT w.watchlist_id, w.user_id, wm.movie_id FROM watchlists w LEFT JOIN watchlist_movies wm ON w.watchlist_id = wm.watchlist_id WHERE w.user_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, userId);
            boolean hasData = false;
            ArrayList<String> movies = new ArrayList<>();
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (!hasData) {
                        wl.setWatchlistId(rs.getInt("watchlist_id"));
                        wl.setUserId(rs.getInt("user_id"));
                        hasData = true;
                    }
                    int movieId = rs.getInt("movie_id");
                    if (!rs.wasNull()) {
                        movies.add(String.valueOf(movieId));
                    }
                }
            }
            
            wl.setMovies(movies);
            if (!hasData) {
                wl.setWatchlistId(userId);
                wl.setUserId(userId);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching watchlists from database");
        }
        return wl;
    }


    public void addWatchlist(int userId) {
        String sql = "INSERT INTO watchlists (name, user_id) VALUES (?,?)";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "Mi Watchlist");
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error creating watchlist");
        }
    }

    public Watchlist addMovie(int movieId, int userId) {
        Watchlist wl = new Watchlist();
        int watchlistId = 0;

        String checkSql = "SELECT watchlist_id FROM watchlists WHERE user_id = ? LIMIT 1";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    watchlistId = rs.getInt("watchlist_id");
                } else {
                    addWatchlist(userId);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error checking watchlist");
        }

        if (watchlistId == 0) {
            try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                 
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        watchlistId = rs.getInt("watchlist_id");
                    }
                }
            } catch (SQLException e) {
                throw ErrorFactory.internal("Error retrieving watchlist after creation");
            }
        }

        String sql = "INSERT INTO watchlist_movies (watchlist_id, movie_id) VALUES (?,?)";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, watchlistId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                // lo ignoramos silenciosamente porque la película ya está en la watchlist
            } else {
                throw ErrorFactory.internal("Error adding movie to watchlist");
            }
        }
        return wl;
    }

    public void deleteMovie(int userId, String movieId) {
        String checkSql = "SELECT watchlist_id FROM watchlists WHERE user_id = ?";
        String sql = "DELETE FROM watchlist_movies WHERE watchlist_id = ? AND movie_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
            int watchlistId = 0;
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        watchlistId = rs.getInt("watchlist_id");
                    }
                }
            }

            if (watchlistId > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, watchlistId);
                    stmt.setInt(2, Integer.parseInt(movieId));
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException | NumberFormatException e) {
            throw ErrorFactory.internal("Error removing movie from watchlist");
        }
    }
}