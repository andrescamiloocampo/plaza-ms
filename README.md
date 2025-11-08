# Manual Técnico de Configuración Local

Este manual describe los pasos necesarios para configurar y ejecutar la pila completa del proyecto (tres microservicios de backend y un frontend) en un entorno de desarrollo local.

## 1\. Arquitectura del Proyecto

El proyecto se compone de 4 aplicaciones principales:

1.  **`users-ms` (Microservicio de Usuarios):** Gestiona la autenticación, los usuarios y los roles.
      * Puerto: `8081`
      * Base de datos: MySQL
2.  **`plaza-ms` (Microservicio de Plazas):** Gestiona los restaurantes, platos y pedidos.
      * Puerto: `8082` (Inferido de las configuraciones)
      * Base de datos: MySQL
3.  **`traceabilty-ms` (Microservicio de Trazabilidad):** Registra los logs y el historial de los pedidos.
      * Puerto: `8083`
      * Base de datos: MongoDB (Atlas)
4.  **`plaza-definitive-front` (Aplicación Frontend):** Interfaz de usuario construida en Next.js.
      * Puerto: `3000` (Por defecto en Next.js)

## 2\. Requerimientos Previos

Asegúrate de tener instalado el siguiente software en tu máquina:

  * **Java 17:** Todos los microservicios de backend requieren Java 17 (como se especifica en sus archivos `build.gradle`).
  * **Gradle:** Utilizado para la gestión de dependencias y ejecución de los servicios de backend.
  * **Node.js y npm:** Necesarios para instalar y ejecutar el proyecto frontend (`package.json`).
  * **MySQL (Servidor y Cliente):** Base de datos para los servicios `users-ms` y `plaza-ms`.
  * **Acceso a Internet:** El servicio `traceabilty-ms` se conecta a una base de datos MongoDB Atlas en la nube.

## 3\. Configuración de Bases de Datos

### 3.1. MySQL

Los servicios `users-ms` y `plaza-ms` requieren bases de datos MySQL.

**Paso 1: Crear las Bases de Datos**

Abre tu cliente de MySQL y ejecuta los siguientes comandos:

```sql
CREATE DATABASE users;
CREATE DATABASE plaza;
```

**Paso 2: Datos Semilla**

1.  **Base de Datos `users` (Automático):**
    El microservicio `users-ms` llenará automáticamente la tabla de roles (`role_table`) al arrancar. El archivo `DataInitializer.java` se encarga de crear los roles: `OWNER`, `ADMIN`, `CUSTOMER`, y `EMPLOYEE`. No se requiere acción manual.

2.  **Base de Datos `plaza` (Manual):**
    El microservicio `plaza-ms` necesita que existan categorías para poder crear platos. Debes insertar manualmente algunas categorías.

    Ejecuta el siguiente script en tu base de datos `plaza`:

    ```sql
    USE plaza;

    -- Crear la tabla de categorías (si 'ddl-auto: update' no lo hace)
    CREATE TABLE IF NOT EXISTS categories (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        description VARCHAR(255)
    );

    -- Insertar categorías de ejemplo
    INSERT INTO categories (name, description) VALUES
    ('Comida Rápida', 'Platos de preparación rápida'),
    ('Asiática', 'Platos de origen asiático'),
    ('Mexicana', 'Platos de la cocina mexicana'),
    ('Parrilla', 'Carnes y preparados a la parrilla'),
    ('Postres', 'Opciones dulces'),
    ('Bebidas', 'Refrescos, jugos y licores');
    ```

### 3.2. MongoDB (Traceability)

No se requiere configuración local. El microservicio `traceabilty-ms` está configurado para conectarse a una instancia de MongoDB Atlas hosteada en la nube. Las credenciales y la URL de conexión están definidas en su archivo `application.yml`.

**Asegúrate de tener conexión a internet para que este servicio funcione.**

## 4\. Ejecución de Microservicios (Backend)

Ejecuta los servicios en el siguiente orden. Abre una terminal separada para cada servicio.

### Paso 1: `users-ms` (Puerto 8081)

1.  Navega al directorio raíz del microservicio: `andrescamiloocampo/users-ms/users-ms-49e9d7463ea639fc4445192f5b311bd54a402ef9/`
2.  Verifica tu configuración en `src/main/resources/application.yml`. Por defecto, espera una base de datos `users` en `localhost:3306` con el usuario `root` y contraseña `1234`. Ajusta este archivo si tus credenciales de MySQL son diferentes.
3.  Ejecuta el servicio:
    ```bash
    ./gradlew bootRun
    ```

### Paso 2: `plaza-ms` (Puerto 8082)

1.  Navega al directorio raíz del microservicio: `andrescamiloocampo/plaza-ms/plaza-ms-1df80a594f7804371994e76cc30cffcc99e87c10/`

2.  Este servicio requiere **variables de entorno** para su base de datos (según `application.yml`).

3.  **Antes de ejecutar**, configura las siguientes variables en tu terminal:

      * **En Linux/macOS:**
        ```bash
        export DB_URL=jdbc:mysql://localhost:3306/plaza
        export DB_USERNAME=root
        export DB_PASSWORD=1234
        # (Ajusta USERNAME y PASSWORD a tus credenciales)
        ```
      * **En Windows (Command Prompt):**
        ```cmd
        set DB_URL=jdbc:mysql://localhost:3306/plaza
        set DB_USERNAME=root
        set DB_PASSWORD=1234
        ```

4.  Ejecuta el servicio (en la misma terminal donde configuraste las variables):

    ```bash
    ./gradlew bootRun
    ```

### Paso 3: `traceabilty-ms` (Puerto 8083)

1.  Navega al directorio raíz del microservicio: `andrescamiloocampo/traceabilty-ms/traceabilty-ms-58ae02f6f2ec9d069a1e6db6b101425730c62d8d/`
2.  Este servicio se conectará a MongoDB Atlas usando la configuración de `src/main/resources/application.yml`. No requiere configuración adicional.
3.  Ejecuta el servicio:
    ```bash
    ./gradlew bootRun
    ```

## 5\. Ejecución del Frontend

1.  Navega al directorio raíz del frontend: `andrescamiloocampo/plaza-definitive-front/plaza-definitive-front-96dbe6133df91c85ad164391ccd5642f2d465fd8/`

2.  Instala las dependencias de Node.js:

    ```bash
    npm install
    ```

3.  **¡IMPORTANTE\! Configuración de URLs del API:**
    El código del frontend tiene una discrepancia. Apunta al servicio de usuarios en el puerto `8080`, pero el servicio `users-ms` corre en el puerto `8081`.
    Para corregir esto, crea un archivo llamado `.env.local` en la raíz del proyecto frontend (`plaza-definitive-front`).

4.  Añade el siguiente contenido al archivo `.env.local`:

    ```properties
    NEXT_PUBLIC_USERS_HOST=http://localhost:8081/api/v1
    NEXT_PUBLIC_PLAZA_HOST=http://localhost:8082/api/v1
    NEXT_PUBLIC_LOGS_HOST=http://localhost:8083/api/v1
    ```

5.  Ejecuta el servidor de desarrollo:

    ```bash
    npm run dev
    ```

## 6\. Verificación

Si todos los pasos se completaron correctamente, tendrás el sistema completo corriendo en:

  * **Frontend:** [http://localhost:3000](https://www.google.com/search?q=http://localhost:3000)
  * **Users API:** [http://localhost:8081](https://www.google.com/search?q=http://localhost:8081)
  * **Plaza API:** [http://localhost:8082](https://www.google.com/search?q=http://localhost:8082)
  * **Traceability API:** [http://localhost:8083](https://www.google.com/search?q=http://localhost:8083)

Puedes acceder a la aplicación frontend desde tu navegador en `http://localhost:3000` y registrar un nuevo usuario para comenzar.
