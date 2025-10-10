package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	private static HikariDataSource ds;
	
	static {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/fatmovies");
        config.setUsername("java_user");
        config.setPassword("java_pass");
        config.setMaximumPoolSize(10);    // ajustar según la app/servidor
        config.setMinimumIdle(2);
        config.setPoolName("FatMoviesPool");
        config.setConnectionTimeout(30000); // ms
        config.setIdleTimeout(600000);      // ms
        config.setMaxLifetime(1800000);     // ms

        // optimizaciones opcionales
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
    }
	
	public static DataSource getDataSource() {
		
        return ds;
    }

    public static void shutdown() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }
    
    
    
    
	/*
    //private static final String URL = "jdbc:mysql://localhost:3306/fatmovies"; 
    private static final String URL = "jdbc:mysql://localhost:3307/fatmovies";
    private static final String USERNAME = "java_user";
    private static final String PASSWORD = "java_pass"; 
    
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Conexión a base de datos establecida correctamente");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL Driver not found", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a base de datos cerrada");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    */
}