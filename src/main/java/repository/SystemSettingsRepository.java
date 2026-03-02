package repository;

import java.util.List;
import entity.SystemSettings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import util.DataSourceProvider;
import exception.ErrorFactory;
import java.sql.SQLException;
import java.util.ArrayList;

public class SystemSettingsRepository {

    //Select base para no repetir las columnas
    private static final String BASE_SELECT = "SELECT config_id, normal_watchlist_limit, active_watchlist_limit, effective_date, admin_user_id, kcals_to_level_2, kcals_to_level_3, kcals_to_level_4 FROM system_settings ";

    public SystemSettings getLast() {
        SystemSettings config = null;
        String sql = BASE_SELECT + "ORDER BY config_id DESC LIMIT 1";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                config = mapResultSetToSystemSettings(rs);
            }

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching configuration from database");
        }
        return config;
    }

    public SystemSettings add(SystemSettings config) {
        String sql = "INSERT INTO system_settings (normal_watchlist_limit, active_watchlist_limit, admin_user_id, kcals_to_level_2, kcals_to_level_3, kcals_to_level_4) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getNormalWatchlistLimit());
            stmt.setInt(2, config.getActiveWatchlistLimit());
            
            if (config.getAdminUserId() != null) {
                stmt.setInt(3, config.getAdminUserId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(4, config.getKcalsToLevel2());
            stmt.setInt(5, config.getKcalsToLevel3());
            stmt.setInt(6, config.getKcalsToLevel4());

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error creating configuration in database");
        }
        return config;
    }

    public List<SystemSettings> getAll() {
        List<SystemSettings> configs = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY effective_date DESC";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                configs.add(mapResultSetToSystemSettings(rs));
            }

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching configurations from database");
        }
        return configs;
    }

    // ÚNICO PUNTO DE MAPEO
    private SystemSettings mapResultSetToSystemSettings(ResultSet rs) throws SQLException {
        SystemSettings config = new SystemSettings();
        config.setConfigId(rs.getInt("config_id"));
        config.setNormalWatchlistLimit(rs.getInt("normal_watchlist_limit"));
        config.setActiveWatchlistLimit(rs.getInt("active_watchlist_limit"));
        config.setEffectiveDate(rs.getString("effective_date"));
        
        Object adminIDObj = rs.getObject("admin_user_id");
        if (adminIDObj != null) {
            config.setAdminUserId(((Number) adminIDObj).intValue());
        }
        
        config.setKcalsToLevel2(rs.getInt("kcals_to_level_2"));
        config.setKcalsToLevel3(rs.getInt("kcals_to_level_3"));
        config.setKcalsToLevel4(rs.getInt("kcals_to_level_4"));
        
        return config;
    }
}