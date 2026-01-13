package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import model.Categoria; // IMPORTANTE
import model.Producto;
import util.JPAUtil;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProductoController implements Serializable {

    private List<Producto> listaProductos;
    private List<Categoria> listaCategorias; // <--- Nueva lista
    private Producto productoSeleccionado;

    @PostConstruct
    public void init() {
        this.productoSeleccionado = new Producto();
        cargarDatos();
    }

    public void cargarDatos() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();

            // 1. Cargar Productos
            listaProductos = em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();

            // 2. Cargar Categorías (NUEVO)
            listaCategorias = em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public void prepararNuevo() {
        this.productoSeleccionado = new Producto();
    }

    public void guardar() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            if (productoSeleccionado.getId() == null) {
                // Validación extra: Fecha de registro automática si no la tiene
                if(productoSeleccionado.getFechaRegistro() == null) {
                    productoSeleccionado.setFechaRegistro(java.time.LocalDate.now());
                }
                em.persist(productoSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto creado"));
            } else {
                em.merge(productoSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto actualizado"));
            }

            em.getTransaction().commit();

            this.productoSeleccionado = new Producto();
            cargarDatos(); // Recargar tablas

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar. Verifica que todos los campos estén llenos."));
        } finally {
            if (em != null) em.close();
        }
    }

    public void eliminar() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, productoSeleccionado.getId());
            if (p != null) {
                em.remove(p);
                em.getTransaction().commit();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto eliminado"));
            }
            this.productoSeleccionado = new Producto();
            cargarDatos();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar."));
        } finally {
            if (em != null) em.close();
        }
    }

    // Getters y Setters
    public List<Producto> getListaProductos() { return listaProductos; }
    public List<Categoria> getListaCategorias() { return listaCategorias; } // <--- NUEVO GETTER
    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) { this.productoSeleccionado = productoSeleccionado; }
}