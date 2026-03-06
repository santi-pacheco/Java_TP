package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.ResourceBundle;

import javax.sql.DataSource;

public class DataSourceProvider {
    private static volatile HikariDataSource ds;
    private static String DB_USER;
    private static String DB_PASS;
    private static String DB_URL;
    private DataSourceProvider() { /* no instancias */ }

    public static DataSource getDataSource() {
        if (ds == null) {
            synchronized (DataSourceProvider.class) {
                if (ds == null) {
                    try {
                        ResourceBundle configProp = ResourceBundle.getBundle("config");
                    	DB_USER = configProp.getString("DB_USER").trim();
                        DB_PASS = configProp.getString("DB_PASS").trim();
                        DB_URL = configProp.getString("DB_URL").trim();
                        
                        System.out.println("✓ DataSourceProvider: DB_USER=" + DB_USER);
                        System.out.println("✓ DataSourceProvider: DB_PASS=" + DB_PASS);
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                       
                        config.setJdbcUrl("jdbc:" + DB_URL + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                        config.setUsername(DB_USER);
                        config.setPassword(DB_PASS);

                        config.setMaximumPoolSize(10);
                        config.setMinimumIdle(2);
                        config.setPoolName("FatMoviesPool");
                        config.setConnectionTimeout(30000);
                        config.setIdleTimeout(600000);
                        config.setMaxLifetime(1800000);

                        config.addDataSourceProperty("cachePrepStmts", "true");
                        config.addDataSourceProperty("prepStmtCacheSize", "250");
                        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                        config.addDataSourceProperty("useUnicode", "true");
                        config.addDataSourceProperty("characterEncoding", "utf8");

                        ds = new HikariDataSource(config);
                        System.out.println("✓ DataSourceProvider initialized successfully.");
                    } catch (Exception e) {
                        System.err.println("Database connection failed: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Error inicializando DataSource", e);
                    }
                }
            }
        }
        return ds;
    }

    public static void shutdown() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }
}