package converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Producto;

@Named
@FacesConverter(value = "productoConverter", managed = true)
public class ProductoConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || value.equals("null")) {
            return null;
        }
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("AlmacenPU");
            em = emf.createEntityManager();
            // Asegurar que la entidad Producto use "Long" para el ID
            return em.find(Producto.class, Long.valueOf(value));
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Producto) {
            Producto p = (Producto) value;
            return (p.getId() != null) ? String.valueOf(p.getId()) : null;
        }
        return "";
    }
}
