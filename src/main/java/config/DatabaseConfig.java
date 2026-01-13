package config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@DataSourceDefinition(
        name = "java:app/jdbc/AlmacenDS",        // Nombre del enchufe
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "postgres",
        password = "@=6z5Tj7*",
        databaseName = "warehouseDemoProject",
        serverName = "localhost",
        portNumber = 5433                        // Mi puerto personalizado
)
@ApplicationScoped
@Named
public class DatabaseConfig {
}