package listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import service.ReviewModerationService;
import util.DataSourceProvider;

@WebListener
public class AppServletContextListener implements ServletContextListener {

    private static ValidatorFactory validatorFactory;
    private ReviewModerationService moderationService; // Solo para inicializar el pool de threads

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>> La aplicación se ha iniciado.");
        System.out.println("Inicializando el pool de conexiones...");
        System.out.println("Inicializando Validator...");
        try {
            validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();

            ServletContext context = sce.getServletContext();
            context.setAttribute("miValidador", validator);

            System.out.println("Validator creado y guardado en el contexto.");
        } catch (Exception e) {
            System.err.println("!!! Error fatal al inicializar el Validator: " + e.getMessage());
        }

        // Inicializar el servicio de moderación (solo para crear el ExecutorService)
        System.out.println("Inicializando servicio de moderación...");
        moderationService = ReviewModerationService.getInstance();
        System.out.println("Servicio de moderación listo (sin tareas programadas).");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> La aplicación se está deteniendo...");

        if (validatorFactory != null) {
            System.out.println("Cerrando ValidatorFactory...");
            validatorFactory.close();
            System.out.println("ValidatorFactory cerrado.");
        }

        // Cerrar el servicio de moderación
        if (moderationService != null) {
            System.out.println("Cerrando servicio de moderación...");
            ReviewModerationService.getInstance().shutdown();
            System.out.println("Servicio de moderación cerrado.");
        }

        System.out.println("Cerrando el pool de conexiones...");
        DataSourceProvider.shutdown();
        System.out.println("Pool de conexiones cerrado.");

        try {
            System.out.println("Deteniendo hilos del driver JDBC...");
            AbandonedConnectionCleanupThread.checkedShutdown();
            System.out.println("Hilos del driver JDBC detenidos.");
        } catch (Exception e) {
            System.err.println("!!! Error al detener hilos del driver JDBC: " + e.getMessage());
        }
    }
}