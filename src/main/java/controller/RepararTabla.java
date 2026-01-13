package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RepararTabla {

    public static void main(String[] args) {
        //  DATOS DE CONEXIÓN ---
        String url = "jdbc:postgresql://localhost:5433/warehouseDemoProject";
        String user = "postgres";
        String pass = "@=6z5Tj7*";

        // EL COMANDO SQL MÁGICO LOL
        String sqlCommand = "ALTER TABLE categorias ADD COLUMN IF NOT EXISTS nombre VARCHAR(255);";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            System.out.println(">>> CONECTANDO A LA BD PARA REPARAR EL ESQUEMA... <<<");

            // 1. Ejecutar el comando SQL
            stmt.executeUpdate(sqlCommand);

            System.out.println("✅ ESQUEMA REPARADO: Columna 'nombre' agregada a la tabla 'categorias'.");

        } catch (Exception e) {
            System.err.println("❌ ERROR al ejecutar ALTER TABLE. Revisa la contraseña o si Payara está corriendo.");
            e.printStackTrace();
        }
    }
}