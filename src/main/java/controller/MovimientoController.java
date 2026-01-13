package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Movimiento;
import model.Producto;
import util.JPAUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Date;


@Named
@ViewScoped
public class MovimientoController implements Serializable {

    private Movimiento movimiento;
    private List<Movimiento> listaMovimientos;
    private List<Producto> listaProductos; // Para el desplegable

    @PostConstruct
    public void init() {
        this.movimiento = new Movimiento();
        cargarDatos();
    }

    public void cargarDatos() {
        EntityManager em = null; // Ya no se necesita 'emf' aquí
        try {
            // USAMOS LA NUEVA CLASE
            em = util.JPAUtil.getEntityManager();

            // Cargar historial
            listaMovimientos = em.createQuery("SELECT m FROM Movimiento m ORDER BY m.id DESC", Movimiento.class).getResultList();

            // Cargar productos
            listaProductos = em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
        // Ya no se cierra cerramos el emf, porque se necesita vivo para las siguientes peticiones
    }

    public void registrar() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            // 1. Validaciones básicas de que haya cantidad
            if (movimiento.getCantidad() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cantidad debe ser mayor a 0."));

                return;
            }

            // 2. Traer el producto ACTUALIZADO de la BD (para saber el stock real al instante)
            Producto productoBD = em.find(Producto.class, movimiento.getProducto().getId());

            // 3. Lógica de Inventario (Suma y Resta)
            if ("ENTRADA".equals(movimiento.getTipo())) {
                // Si es Entrada, solo sumamos
                productoBD.setCantidad(productoBD.getCantidad() + movimiento.getCantidad());

            } else if ("SALIDA".equals(movimiento.getTipo())) {
                // ===> AQUÍ ESTÁ LA VALIDACIÓN CRÍTICA <===
                // Verificamos si lo que se quiere sacar es mayor a lo que se tiene
                if (productoBD.getCantidad() < movimiento.getCantidad()) {

                    // Si entra aquí, DETENEMOS TODO
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Stock insuficiente. Solo tienes: " + productoBD.getCantidad()));

                    em.getTransaction().rollback(); // Cancelamos la transacción
                    return; // ¡IMPORTANTE! Este return hace que el código se salga y no guarde nada.
                }

                // Si hay stock, restamos
                productoBD.setCantidad(productoBD.getCantidad() - movimiento.getCantidad());
            }

            // 4. Preparar y Guardar
            movimiento.setProducto(productoBD);
            movimiento.setFecha(java.time.LocalDate.now()); // Usamos LocalDate

            em.persist(movimiento); // Guarda en historial
            em.merge(productoBD);   // Actualiza el stock en producto

            em.getTransaction().commit(); // Confirma los cambios en la BD

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Movimiento registrado correctamente."));

            // 5. Limpiar y Refrescar
            this.movimiento = new model.Movimiento();
            cargarDatos();

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el movimiento."));
        } finally {
            if (em != null) em.close();
        }
    }

    // Getters y Setters
    public Movimiento getMovimiento() { return movimiento; }
    public void setMovimiento(Movimiento movimiento) { this.movimiento = movimiento; }
    public List<Movimiento> getListaMovimientos() { return listaMovimientos; }
    public List<Producto> getListaProductos() { return listaProductos; }
}
