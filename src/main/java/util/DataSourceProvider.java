package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceProvider {
    private static volatile HikariDataSource ds;

    private DataSourceProvider() { /* no instancias */ }

    public static DataSource getDataSource() {
        if (ds == null) {
            synchronized (DataSourceProvider.class) {
                if (ds == null) {
                    try {
                        // fuerza la carga del driver en el classloader correcto
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
<<<<<<< HEAD
<<<<<<< HEAD
                        config.setJdbcUrl("jdbc:mysql://localhost:3306/fatmovies");
=======
                        config.setJdbcUrl("jdbc:mysql://localhost:3307/fatmovies?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
>>>>>>> origin/main
=======
                        config.setJdbcUrl("jdbc:mysql://localhost:3307/fatmovies?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
>>>>>>> 476454e79e29f2f5f0e237eaa86d6f17ae42e706
                        config.setUsername("java_user");
                        config.setPassword("java_pass");

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
                    } catch (Exception e) {
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
