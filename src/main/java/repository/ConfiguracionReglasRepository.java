package repository;
import java.util.List;
import entity.ConfiguracionReglas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import util.DataSourceProvider;
import exception.ErrorFactory;
import java.sql.SQLException;
import java.util.ArrayList;
public class ConfiguracionReglasRepository {

	public ConfiguracionReglas getLast() {
		ConfiguracionReglas config = null;
		String sql = "SELECT configID, umbralResenasActivos, limiteWatchlistNormal, limiteWatchlistActivo, fechaEfectiva, usuarioAdminID FROM configuracionreglas ORDER BY configID DESC LIMIT 1";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			
			if (rs.next()) {
				config = new ConfiguracionReglas();
				config.setConfigID(rs.getInt("configID"));
				config.setUmbralResenasActivo(rs.getInt("umbralResenasActivos"));
				config.setLimiteWatchlistNormal(rs.getInt("limiteWatchlistNormal"));
				config.setLimiteWatchlistActivo(rs.getInt("limiteWatchlistActivo"));
				config.setFechaVigencia(rs.getString("fechaEfectiva"));
				Integer adminID = (Integer) rs.getObject("usuarioAdminID");
				config.setUsuarioAdminID(adminID);
			}
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching configuration from database");
		}
		return config;
	}
	
	public ConfiguracionReglas add(ConfiguracionReglas config) {
	    String sql = "INSERT INTO configuracionreglas (umbralResenasActivos, limiteWatchlistNormal, limiteWatchlistActivo, usuarioAdminID) VALUES (?, ?, ?, ?)";
	    
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, config.getUmbralResenasActivo());
	        stmt.setInt(2, config.getLimiteWatchlistNormal());
	        stmt.setInt(3, config.getLimiteWatchlistActivo());
	        if (config.getUsuarioAdminID() != null) {
	            stmt.setInt(4, config.getUsuarioAdminID());
	        } else {
	            stmt.setNull(4, java.sql.Types.INTEGER);
	        }
	        
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw ErrorFactory.internal("Error creating configuration in database");
	    }
	    return config;
	}
	
	public List<ConfiguracionReglas> getAll() {
	    List<ConfiguracionReglas> configs = new ArrayList<>();
	    String sql = "SELECT configID, umbralResenasActivos, limiteWatchlistNormal, limiteWatchlistActivo, fechaEfectiva, usuarioAdminID FROM configuracionreglas ORDER BY fechaEfectiva DESC";

	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            ConfiguracionReglas config = new ConfiguracionReglas();
	            config.setConfigID(rs.getInt("configID"));
	            config.setUmbralResenasActivo(rs.getInt("umbralResenasActivos"));
	            config.setLimiteWatchlistNormal(rs.getInt("limiteWatchlistNormal"));
	            config.setLimiteWatchlistActivo(rs.getInt("limiteWatchlistActivo"));
	            config.setFechaVigencia(rs.getString("fechaEfectiva"));
	            Integer adminID = (Integer) rs.getObject("usuarioAdminID");
	            config.setUsuarioAdminID(adminID);
	            
	            configs.add(config);
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw ErrorFactory.internal("Error fetching configurations from database: " + e.getMessage());
	    }
	    return configs;
	}
}