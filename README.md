# Sistema de Gestión de Almacén (Demo)

Este proyecto es una aplicación web Java Enterprise CE (Jakarta EE) proyecto demo para la gestión de inventarios. Permite administrar productos, categorías, realizar movimientos de entrada/salida y visualizar reportes con cálculo de valor de inventario.

## Tecnologías Utilizadas

* **Lenguaje:** Java 17 / 21
* **Framework Web:** Jakarta Server Faces (JSF) 2.3+
* **Componentes UI:** PrimeFaces 13.0
* **Persistencia:** JPA / Hibernate
* **Base de Datos:** PostgreSQL 12+
* **Servidor:** Payara Server / GlassFish
* **Reportes:** Apache POI (Excel) y OpenPDF (PDF)
* **Build Tool:** Maven

## 📋 Funcionalidades Principales

1.  **Dashboard:** Vista general con KPIs (Total de productos, Stock total).
2.  **Gestión de Inventario:** CRUD completo de Productos y Categorías.
3.  **Movimientos:** Registro de Entradas y Salidas con validación de stock insuficiente.
4.  **Reportes:** Exportación de datos de consulta a PDF y Excel.
5.  **Seguridad:** Control de acceso mediante Login (Roles: ADMIN/USER).

## ⚙️ Configuración e Instalación

1.  **Base de Datos:**
    * Crear una base de datos en PostgreSQL llamada `almacen_db`.
    * Ejecutar el script `script_almacen.sql` adjunto para crear las tablas.

2.  **Conexión:**
    * Configurar el archivo `src/main/resources/META-INF/persistence.xml`.
    * Actualizar usuario y contraseña de su conexión local a PostgreSQL.

3.  **Ejecución:**
    * Clonar el repositorio.
    * Ejecutar `mvn clean package`.
    * Desplegar el archivo `.war` generado en Payara Server.
    * Acceder a: `http://localhost:8080/almacen-demo`

## 👤 Autor
Desarrollado como entregable final para el módulo de Desarrollo Web Java    .
## Proyecto Finalizado