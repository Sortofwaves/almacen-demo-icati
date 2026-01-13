package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // Esta variable es 'static', lo que significa que solo existe UNA VEZ en toda la aplicación
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            // "AlmacenPU" debe ser el mismo nombre que tienes en tu persistence.xml
            return Persistence.createEntityManagerFactory("AlmacenPU");
        } catch (Throwable ex) {
            System.err.println("Error al crear la fábrica de EntityManager: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Este método es el que llamaremos desde los controladores
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Opcional: Para cerrar la "   fábrica" cuando se apague la aplicación
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
