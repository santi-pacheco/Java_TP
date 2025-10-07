package util;

import java.sql.*;

public class ConnectionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Iniciando prueba de conexión...");

        try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
            // isValid comprueba la conexión (timeout en segundos)
            System.out.println("Conn válida? " + conn.isValid(2));

            DatabaseMetaData md = conn.getMetaData();
            System.out.println("DB: " + md.getDatabaseProductName() + " " + md.getDatabaseProductVersion());
            System.out.println("Driver: " + md.getDriverName() + " " + md.getDriverVersion());
            System.out.println("URL: " + md.getURL());

            // Ejecutar una consulta simple
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT 1")) {
                if (rs.next()) {
                    System.out.println("SELECT 1 -> " + rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión:");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Prueba finalizada.");
	}

}
