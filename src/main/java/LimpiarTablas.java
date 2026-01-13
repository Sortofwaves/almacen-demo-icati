import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class LimpiarTablas {
    public static void main(String[] args) {
        // Tus datos de conexión (sacados de tu persistence.xml)
        String url = "jdbc:postgresql://localhost:5433/warehouseDemoProject";
        String user = "postgres";
        String pass = "@=6z5Tj7*"; // OJO: Si cambiaste la contraseña, pon la correcta aquí.

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            System.out.println(">>> CONECTANDO A BASE DE DATOS... <<<");

            // Borramos la tabla problemática
            String sql = "DROP TABLE IF EXISTS categorias CASCADE";
            stmt.executeUpdate(sql);

            System.out.println("✅ Tabla 'categorias' borrada exitosamente.");
            System.out.println("Ahora Payara podrá crearla de nuevo con las columnas correctas.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
