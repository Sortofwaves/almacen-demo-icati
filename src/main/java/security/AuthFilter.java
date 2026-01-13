package security;

import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import controller.LoginController;
import java.io.IOException;

// Este filtro vigila todas las páginas que terminen en .xhtml
@WebFilter(filterName = "AuthFilter", urlPatterns = {"*.xhtml"})
public class AuthFilter implements Filter {

    @Inject
    private LoginController loginController;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nada que inicializar por ahora
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Obtenemos la URL que el usuario intenta visitar
        String reqURI = req.getRequestURI();

        // REGLAS DE ACCESO:
        // 1. ¿Está intentando ir a la página de login? -> DEJAR PASAR
        // 2. ¿Tiene un usuario logueado en la sesión? -> DEJAR PASAR
        // 3. ¿Está intentando acceder a recursos (imágenes, CSS, JSF resources)? -> DEJAR PASAR
        // 4. Si no cumple nada de lo anterior -> ¡ALTO! MANDAR AL LOGIN

        boolean esLogin = reqURI.contains("/login.xhtml");
        // Verifica si hay una sesión activa y si el usuario ya se guardó
        boolean estaLogueado = (loginController != null && loginController.getUsuarioLogueado() != null);
        boolean esRecurso = reqURI.contains("/jakarta.faces.resource/");

        if (esLogin || estaLogueado || esRecurso) {
            // Adelante!
            chain.doFilter(request, response);
        } else {
            // ¡Alto! Sin permiso. (Regresa al login)
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
        }
    }

    @Override
    public void destroy() {
        // Limpieza (si fuera necesaria)
    }
}
