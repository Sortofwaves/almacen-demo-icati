package controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.hibernate.Session;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;

@Named
@RequestScoped
public class ReporteController {

    static {
        // Esto obliga a JasperReports a usar el compilador JDT que sí funciona en Payara
        System.setProperty("net.sf.jasperreports.compiler.class", "net.sf.jasperreports.engine.design.JRJdtCompiler");
    }

    // --- ACCIONES PDF ---
    public void generarPdfInventario() { ejecutarReporte("inventario.jrxml", "Inventario.pdf", "PDF"); }
    public void generarPdfMovimientos() { ejecutarReporte("movimientos.jrxml", "Movimientos.pdf", "PDF"); }
    public void generarPdfProductos() { ejecutarReporte("productos.jrxml", "Productos.pdf", "PDF"); }
    public void generarPdfCategorias() { ejecutarReporte("categorias.jrxml", "Categorias.pdf", "PDF"); }

    // --- ACCIONES EXCEL ---
    public void generarXlsInventario() { ejecutarReporte("inventario.jrxml", "Inventario.xlsx", "XLS"); }
    public void generarXlsMovimientos() { ejecutarReporte("movimientos.jrxml", "Movimientos.xlsx", "XLS"); }
    public void generarXlsProductos() { ejecutarReporte("productos.jrxml", "Productos.xlsx", "XLS"); }
    public void generarXlsCategorias() { ejecutarReporte("categorias.jrxml", "Categorias.xlsx", "XLS"); }

    // --- LÓGICA CENTRAL ---
    private void ejecutarReporte(String archivoJasper, String nombreSalida, String tipoFormato) {
        System.out.println(">>> Iniciando generación de reporte: " + archivoJasper + " en formato: " + tipoFormato);
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("AlmacenPU");
            em = emf.createEntityManager();
            Session session = em.unwrap(Session.class);

            session.doWork(connection -> {
                if ("PDF".equals(tipoFormato)) {
                    try {
                        exportarPdf(connection, archivoJasper, nombreSalida);
                    } catch (Exception e) {
                        throw new RuntimeException("Error exportando PDF: " + e.getMessage(), e);
                    }
                } else {
                    try {
                        exportarExcel(connection, archivoJasper, nombreSalida);
                    } catch (Exception e) {
                        throw new RuntimeException("Error exportando Excel: " + e.getMessage(), e);
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("!!! ERROR CRÍTICO EN CONTROLADOR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) em.close();
            if (emf != null && emf.isOpen()) emf.close();
        }
    }

    private void exportarPdf(Connection conn, String archivo, String salida) throws Exception {
        JasperPrint print = prepararDatos(conn, archivo);

        FacesContext faces = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + salida + "\"");

        ServletOutputStream out = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(print, out);

        out.flush();
        out.close();
        faces.responseComplete();
    }

    private void exportarExcel(Connection conn, String archivo, String salida) throws Exception {
        JasperPrint print = prepararDatos(conn, archivo);

        FacesContext faces = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

        response.reset();
        // Tipo MIME correcto para Excel (.xlsx)
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + salida + "\"");

        ServletOutputStream out = response.getOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setOnePagePerSheet(false);
        config.setDetectCellType(true);
        config.setWhitePageBackground(false);
        config.setRemoveEmptySpaceBetweenRows(true);
        exporter.setConfiguration(config);

        exporter.exportReport();

        out.flush();
        out.close();
        faces.responseComplete();
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA ---
    private JasperPrint prepararDatos(Connection conn, String archivo) throws Exception {
        InputStream stream = getClass().getResourceAsStream("/reportes/" + archivo);
        if (stream == null) {
            throw new Exception("No se encontró el archivo de diseño: /reportes/" + archivo);
        }
        JasperReport report = JasperCompileManager.compileReport(stream);
        // Agregar parámetros para el reporte en este HashMap
        return JasperFillManager.fillReport(report, new HashMap<>(), conn);
    }

}