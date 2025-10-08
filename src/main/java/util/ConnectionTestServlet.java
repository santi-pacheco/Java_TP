package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.DataSourceProvider;

@WebServlet("/connection-test")
public class ConnectionTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("== Prueba de conexión a la base de datos ==");

            // Intentar obtener una conexión
            try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {

                if (conn == null) {
                    out.println("❌ No se pudo obtener una conexión (conn es null)");
                    return;
                }	

                out.println("✅ Conexión obtenida correctamente.");
                out.println("isValid(2): " + conn.isValid(2));

                DatabaseMetaData meta = conn.getMetaData();
                out.println("Base de datos: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
                out.println("Driver: " + meta.getDriverName() + " " + meta.getDriverVersion());

                // Consulta rápida
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT 1")) {
                    if (rs.next()) {
                        out.println("Resultado de 'SELECT 1': " + rs.getInt(1));
                    }
                }

            } catch (Exception e) {
                out.println("❌ Error al intentar conectarse:");
                e.printStackTrace(out);
            }
        }
    }
}
