package listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.DataSourceProvider;

@WebListener
public class AppServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>> La aplicación se ha iniciado. Pool de conexiones listo.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> La aplicación se está deteniendo...");

        // 1. Cerramos el pool de conexiones Hikari de forma ordenada
        System.out.println("Cerrando el pool de conexiones...");
        DataSourceProvider.shutdown();
        System.out.println("Pool de conexiones cerrado.");

        // 2. Detenemos el hilo de limpieza de MySQL
        try {
            System.out.println("Deteniendo hilos del driver JDBC...");
            AbandonedConnectionCleanupThread.checkedShutdown();
            System.out.println("Hilos del driver JDBC detenidos.");
        } catch (Exception e) {
            System.err.println("!!! Error al detener hilos del driver JDBC: " + e.getMessage());
        }
    }
}