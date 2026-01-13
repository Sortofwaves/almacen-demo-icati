import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Rol;
import model.Usuario;

public class Principal {  // <--- ¡Fíjate que ahora la clase se llama Principal!
    public static void main(String[] args) {
        System.out.println(">>> ESTOY EJECUTANDO EL CODIGO NUEVO (CLASE PRINCIPAL) <<<");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlmacenPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // 1. Crear un Rol nuevo
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMIN");
            em.persist(rolAdmin);

            // 2. Crear un Usuario nuevo
            Usuario usuario = new Usuario();
            usuario.setUsername("admin_principal");
            usuario.setPassword("secreto123");

            // Conectar Rol al Usuario
            usuario.setRol(rolAdmin);

            em.persist(usuario);

            em.getTransaction().commit();

            System.out.println("--------------------------------------------");
            System.out.println("¡DATOS GUARDADOS CORRECTAMENTE! ✅");
            System.out.println("Se creó el Rol 'ADMIN' y el usuario 'admin_principal'.");
            System.out.println("--------------------------------------------");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}