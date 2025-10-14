# Album Virtual de Figuritas

## Descripción del proyecto
Este es un **modulo backend** de una aplicación desarrollada con **Spring Boot** que permite gestionar álbumes y sus figuritas asociadas.
El sistema ofrece **endpoints REST** para crear, consultar y publicar álbumes, con persistencia en base de datos relacional (**MySQL**) mediante **Spring Data JPA**.

El proyecto fue desarrollado como parte de un *trabajo práctico académico* y aplica conceptos de:
- Diseño en capas
- Inyección de dependencias
- Buenas prácticas con Spring Boot 3

También incluye:
- **Autenticación** con *Spring Security*  
- **Persistencia** con *Spring Data JPA*  
- **Documentación** y pruebas con *Postman* y *Spring Boot Test*

---

## Tecnologías utilizadas
☕ Java 17
🧩 Spring Boot 3.5.6
🗄️ Spring Data JPA
🔐 Spring Security
🐬 MySQL
🧰 Lombok
⚙️ Maven
🧪 Spring Boot Test + REST Docs para pruebas y documentación

📦 Para ejecutar los tests:
```bash
mvn test
```

---

## Configuración y Ejecución del proyecto
### 1️⃣ Requisitos previos
Tener instalado:
- **Java 17+**
- **Maven 3.9+**
- **MySQL** (base de datos en ejecución)

### 2️⃣ Configurar la base de datos
En el archivo 📁 `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/album_db
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```
> 💡 **Nota:** reemplazá `<TU_USUARIO>` y `<TU_CONTRASEÑA>` por tus credenciales locales.

### 3️⃣ Ejecutar la aplicación
Desde la raíz del proyecto:
```bash
mvn spring-boot:run
```

La API estará disponible en:
```bash
http://localhost:8080
```
```bash
http://localhost:8080/swagger-ui/index.html
```
