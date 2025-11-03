package listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import util.DataSourceProvider;

@WebListener
public class AppServletContextListener implements ServletContextListener {

	private static ValidatorFactory validatorFactory;
	
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>> La aplicación se ha iniciado.");
        System.out.println("Inicializando el pool de conexiones...");
        System.out.println("Inicializando Validator...");
        try {
            validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            
            // Guardar el Validator en el contexto de la aplicación
            ServletContext context = sce.getServletContext();
            context.setAttribute("miValidador", validator); // <-- Clave importante
            
            System.out.println("Validator creado y guardado en el contexto.");
        } catch (Exception e) {
            System.err.println("!!! Error fatal al inicializar el Validator: " + e.getMessage());
            // Aquí podrías querer detener la aplicación si la validación es crítica
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> La aplicación se está deteniendo...");
        
        if (validatorFactory != null) {
            System.out.println("Cerrando ValidatorFactory...");
            validatorFactory.close();
            System.out.println("ValidatorFactory cerrado.");
        }
        
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