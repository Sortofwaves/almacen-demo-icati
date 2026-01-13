package controller; // O controller, según donde lo tengas

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Categoria;

@Named
@FacesConverter(value = "categoriaConverter", managed = true)
public class CategoriaConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("--- CONVERTIDOR: Intentando convertir ID: '" + value + "' a Objeto ---");

        if (value == null || value.trim().isEmpty() || value.equals("null")) {
            System.out.println("--- CONVERTIDOR: Valor vacío o nulo, retornando null ---");
            return null;
        }

        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("AlmacenPU");
            em = emf.createEntityManager();

            Long id = Long.valueOf(value);
            Categoria cat = em.find(Categoria.class, id);

            if (cat != null) {
                System.out.println("--- CONVERTIDOR: ¡ÉXITO! Encontrada categoría: " + cat.getNombre() + " ---");
                return cat;
            } else {
                System.out.println("--- CONVERTIDOR: ERROR. No existe categoría con ID: " + id + " en la BD ---");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("--- CONVERTIDOR: Error de formato. '" + value + "' no es un número ---");
            return null;
        } catch (Exception e) {
            System.out.println("--- CONVERTIDOR: Error grave de conexión ---");
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) return "";
        if (value instanceof Categoria) {
            return String.valueOf(((Categoria) value).getId());
        }
        return value.toString();
    }
}