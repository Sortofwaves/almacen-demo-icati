import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Rol;      
import model.Usuario;  
import org.omnifaces.util.Faces;
public class Main {
    public static void main(String[] args) {
        System.out.println(">>> ESTOY EJECUTANDO EL CODIGO NUEVO <<<");

        // Usamos create-drop, así que al arrancar CREA las tablas
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlmacenPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // 1. Crear Rol
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMIN");
            em.persist(rolAdmin);

            // 2. Crear Usuario
            Usuario usuario = new Usuario();
            usuario.setUsername("admin_principal");
            usuario.setPassword("secreto123");
            usuario.setRol(rolAdmin);
            em.persist(usuario);

            em.getTransaction().commit();

            System.out.println("¡DATOS GUARDADOS CORRECTAMENTE! ✅");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
            // Al cerrarse aquí, como se usa 'create-drop', Hibernate BORRARÁ las tablas.
        }
    }
}
