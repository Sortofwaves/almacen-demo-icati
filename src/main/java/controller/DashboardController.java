package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import util.JPAUtil; // Usamos la utilidad
import java.io.Serializable;

@Named
@ViewScoped
public class DashboardController implements Serializable {

    private long totalProductos;
    private long totalStock;

    @PostConstruct
    public void init() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();

            // 1. Contar cuántos productos hay en total
            // "SELECT COUNT(p) FROM Producto p" devuelve un Long
            this.totalProductos = em.createQuery("SELECT COUNT(p) FROM Producto p", Long.class)
                    .getSingleResult();

            // 2. Sumar el stock de todos los productos
            // "SELECT SUM(p.cantidad) FROM Producto p" devuelve Long o Null
            Long sumaStock = em.createQuery("SELECT SUM(p.cantidad) FROM Producto p", Long.class)
                    .getSingleResult();

            // Si no hay productos, la suma es null, así que se convierte a 0
            this.totalStock = (sumaStock != null) ? sumaStock : 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    // Getters
    public long getTotalProductos() { return totalProductos; }
    public long getTotalStock() { return totalStock; }
}
