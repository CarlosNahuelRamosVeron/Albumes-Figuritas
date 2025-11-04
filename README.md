# Álbum Virtual de Figuritas

## Descripción del proyecto
Este es un **modulo backend** de una aplicación desarrollada con **Spring Boot** que permite gestionar álbumes y sus figuritas asociadas.
El sistema ofrece **endpoints REST** para crear, consultar y publicar álbumes, con persistencia en base de datos relacional (**MySQL**) mediante **Spring Data JPA**.

El proyecto fue desarrollado como parte de un trabajo práctico académico y aplica conceptos de:
- Diseño en capas
- Inyección de dependencias
- Buenas prácticas con Spring Boot 3

También incluye:
- Autenticación con Spring Security
- Persistencia con Spring Data JPA
- Documentación y pruebas con Postman y Spring Boot Test

---

## Tecnologías
- Java 17
- Spring Boot 3.5.6 (Web, Security, Data JPA, Validation)
- MySQL 8 / H2 (perfil de memoria)
- JWT (JJWT)
- Springdoc OpenAPI (Swagger UI)
- Maven
- Lombok

---

## Perfiles y propiedades
- Perfil por defecto (sin especificar): MySQL
  - Archivo: `album/src/main/resources/application.properties`
  - URL por defecto: `jdbc:mysql://localhost:3306/albumdb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC`
  - Usuario/clave por defecto: `root` / `root`
  - DDL auto: `update`
- Perfil `h2`: base en memoria
  - Archivo: `album/src/main/resources/application-h2.properties`
  - Consola H2: `http://localhost:8080/h2-console`
  - JDBC en consola: `jdbc:h2:mem:albumdb` — user: `sa` — pass: `sa`

Swagger UI (ambos perfiles):
- http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

---

## Cómo levantar con Docker
Requisitos: Docker y Docker Compose (plugin `docker compose`). Ejecutar los comandos desde la carpeta `album/`.

- MySQL (app + base en contenedores):
  ```bash
  cd album
  docker compose --profile mysql up --build
  ```
  - Servicios: `db` (MySQL 8) y `app`.
  - Puertos: API en `http://localhost:8080`, MySQL en `localhost:3306`.
  - Volúmenes: `db_data` (datos MySQL) y `uploads_data` (archivos subidos de la app).

- H2 (solo app, base en memoria):
  ```bash
  cd album
  docker compose --profile h2 up --build
  ```
  - API: `http://localhost:8080`
  - Consola H2: `http://localhost:8080/h2-console` (ver credenciales arriba).

- Parar y limpiar:
  ```bash
  # Parar
  docker compose --profile mysql down
  docker compose --profile h2 down

  # Limpiar volúmenes (borra datos de MySQL y uploads)
  docker compose --profile mysql down -v
  ```

---

## Cómo ejecutar local con Spring (sin Docker)
Ejecutar los comandos desde `album/`.

- Requisitos: Java 17, Maven 3.9+. Para MySQL local, tener un servidor MySQL ejecutando y un schema `albumdb` (usuario/clave por defecto `root/root` o ajusta `application.properties`).

- MySQL (perfil por defecto):
  ```bash
  cd album
  # (opcional) crear base si no existe
  mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS albumdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

  # correr
  mvn spring-boot:run
  # o
  mvn -DskipTests package && java -jar target/album-0.0.1-SNAPSHOT.jar
  ```
  Overwrites opcionales por línea de comando/env:
  ```bash
  SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/albumdb" \
  SPRING_DATASOURCE_USERNAME=root \
  SPRING_DATASOURCE_PASSWORD=root \
  mvn spring-boot:run
  ```

- H2 (perfil en memoria):
  ```bash
  cd album
  mvn spring-boot:run -Dspring-boot.run.profiles=h2
  # o con jar
  mvn -DskipTests package && java -jar target/album-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2
  ```
  URLs útiles: API `http://localhost:8080`, Swagger `http://localhost:8080/swagger-ui/index.html`, Consola H2 `http://localhost:8080/h2-console`.

---

## Autenticación y usuarios
- Seguridad con JWT.
- Login: `POST /auth/login` con body `{ "username": "...", "password": "..." }`.
- Usar el token en `Authorization: Bearer <token>`.

---

## Datos de ejemplo (WIP)
Hay un script para poblar usuarios, álbumes y contenidos anidados.

- Requisitos: bash, curl y opcionalmente `jq` (si no hay jq, usa python3).
- Uso (con el server ya levantado en `http://localhost:8080`):
  ```bash
  cd album
  chmod +x scripts/seed.sh
  ./scripts/seed.sh
  # variables opcionales: BASE_URL, ADMIN_USER, ADMIN_PASS, USER_USER, USER_PASS, DEBUG=true
  ```

El script:
- Crea usuarios `admin/admin123` (rol ADMIN) y `user/user123` (rol USER).
- Hace login, crea álbumes de ejemplo y carga contenidos (composición de secciones/figuritas), y publica uno.

---

## Tests
Los tests usan H2 (`application-test.properties`).
```bash
cd album
mvn test
```

---

## Endpoints útiles
- API base: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Consola H2 (solo perfil h2): http://localhost:8080/h2-console

---

## Soluciones a problemas conocidos
- Puerto 8080 ocupado: cambia `SERVER_PORT` (Docker) o `server.port` (local).
- Error de conexión a MySQL: verifica credenciales en `album/src/main/resources/application.properties` y que exista la base `albumdb`.
- Swagger devuelve 401: primero hacé login y usá el botón “Authorize” en Swagger con el token JWT.
