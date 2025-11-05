package repository;

import entity.ConfiguracionReglas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import util.DataSourceProvider;
import exception.ErrorFactory;
import java.sql.SQLException;

public class ConfiguracionReglasRepository {

	public ConfiguracionReglas getLast() {
		ConfiguracionReglas config = null;
		String sql = "SELECT configID, umbralResenasActivo, limiteWatchlistNormal, limiteWatchlistActivo, max(fechaVigencia), usuarioAdminID FROM ConfiguracionReglas";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			
			if (rs.next()) {
				config = new ConfiguracionReglas();
				config.setConfigID(rs.getInt("configID"));
				config.setUmbralResenasActivo(rs.getInt("UmbralResenasActivo"));
				config.setLimiteWatchlistNormal(rs.getInt("limiteWatchlistNormal"));
				config.setLimiteWatchlistActivo(rs.getInt("limiteWatchlistActivo"));
				config.setFechaVigencia(rs.getString("max(fechaVigencia)"));
				config.setUsuarioAdminID(rs.getInt("usuarioAdminID"));
			}
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching configuration from database");
		}
		return config;
	}
	
	public ConfiguracionReglas add(ConfiguracionReglas config) {
	    String sql = "INSERT INTO ConfiguracionReglas (umbralResenasActivo, limiteWatchlistNormal, limiteWatchlistActivo, usuarioAdminID) VALUES (?, ?, ?, ?)";
	    
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, config.getUmbralResenasActivo());
	        stmt.setInt(2, config.getLimiteWatchlistNormal());
	        stmt.setInt(3, config.getLimiteWatchlistActivo());
	        stmt.setInt(4, config.getUsuarioAdminID());
	        
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw ErrorFactory.internal("Error creating configuration in database");
	    }
	    return config;
	}
	
}