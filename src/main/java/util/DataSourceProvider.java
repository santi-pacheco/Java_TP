package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceProvider {
	private static HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3307/fatmovies");
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
}
