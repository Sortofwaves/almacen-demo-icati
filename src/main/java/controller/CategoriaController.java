package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Categoria;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped // Mantiene los datos vivos mientras permanezca en la pantalla de Categorías
public class CategoriaController implements Serializable {

    private Categoria categoria = new Categoria();
    private List<Categoria> listaCategorias;

    // Este método se ejecuta automáticamente al entrar a la página
    @PostConstruct
    public void init() {
        cargarCategorias();
    }

    // --- LÓGICA DE NEGOCIO ---

    private void cargarCategorias() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("AlmacenPU");
            em = emf.createEntityManager();
            // Traer todas las categorías de la BD
            listaCategorias = em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public void guardar() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("AlmacenPU");
            em = emf.createEntityManager();

            em.getTransaction().begin();

            // Si tiene ID, es una edición (Merge). Si no, es nuevo (Persist).
            if (categoria.getId() != null) {
                em.merge(categoria);
            } else {
                em.persist(categoria);
            }

            em.getTransaction().commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Categoría Guardada"));

            // Limpiar formulario y recargar la tabla para ver el cambio
            categoria = new Categoria();
            cargarCategorias();

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la categoría."));
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public void eliminar(Categoria cat) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("AlmacenPU");
            em = emf.createEntityManager();

            em.getTransaction().begin();

            // Apartado para encontrar el objeto en esta sesión antes de borrarlo
            Categoria c = em.find(Categoria.class, cat.getId());
            if (c != null) {
                em.remove(c);
                em.getTransaction().commit();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Categoría eliminada."));
            } else {
                em.getTransaction().rollback();
            }

            cargarCategorias();

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar (¿Tiene productos asociados?)"));
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }


    // Método para limpiar el formulario antes de crear una nueva categoria
    public void prepararNuevo() {
        this.categoria = new Categoria();
    }


    // --- GETTERS Y SETTERS ---

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public List<Categoria> getListaCategorias() { return listaCategorias; }
    // No necesitamos setListaCategorias, porque la cargamos de la BD
}