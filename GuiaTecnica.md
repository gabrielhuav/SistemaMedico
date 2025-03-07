# 📖 Guía Técnica Básica: Sistema Médico

## 🩺 Descripción General del Sistema
El Sistema Médico es una aplicación diseñada para gestionar información de pacientes, consultas y personal de salud. Proporciona funcionalidades clave como:
- **Gestión de pacientes:** Registro, actualización y eliminación de datos.
- **Administración de consultas:** Agendamiento y seguimiento de citas médicas.
- **Manejo de personal médico:** Registro y gestión de información de doctores y enfermeros.
- **APIs RESTful:** Para interactuar con el sistema de manera programática.

## 🔒 Implementación Técnica

### 🔑 Login y Manejo de Roles

**Login:**
El sistema implementa un mecanismo de autenticación donde los usuarios pueden iniciar sesión utilizando credenciales válidas. El proceso de login generalmente involucra:
- Validación de credenciales ingresadas contra las almacenadas en la base de datos.
- Generación de un token de sesión (por ejemplo, JWT) para mantener la sesión activa.

**Manejo de Roles:**
El sistema maneja diferentes roles de usuarios (e.g., administrador, doctor, paciente) para restringir el acceso a ciertas funcionalidades. Los detalles de implementación típicamente incluyen:
- Definición de roles y permisos en la base de datos.
- Middleware para verificar los permisos de los usuarios antes de permitir el acceso a rutas protegidas.

### 🗄️ Conexiones a la Base de Datos
El sistema usa bases de datos relacionales como MySQL o PostgreSQL. Las configuraciones de conexión se manejan a través de variables de entorno.

**Configuración de Conexión:**
1. Renombra el archivo `.env.example` a `.env`.
2. Configura las credenciales necesarias para la conexión a la base de datos:
   ```env
   DB_HOST=localhost
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=password
   DB_NAME=sistemamedico

###🌱 Configuraciones de Spring Boot
Si hay componentes basados en Spring Boot, las configuraciones típicas incluirían:

  1.- Archivo application.properties o application.yml:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistemamedico
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

```
  2.- Dependencias en pom.xml:
  ```dependency
  XML
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
  </dependency>
```
###🛠️ Instalación y Configuración
  Clonar el repositorio:
  ```env
  sh
  git clone https://github.com/Brandonttt/SistemaMedico.git
  cd SistemaMedico
 ```
  Instalar dependencias: Asegúrate de tener instalado las extensiones para Spring boot y npm o yarn.
 
  sh
  npm install  # o yarn install
Configurar variables de entorno: Renombra el archivo .env.example a .env y configura las credenciales necesarias como la conexión a la base de datos.

Compilar y ejecutar el proyecto:

sh
npm run build  # Compilar
npm start      # Ejecutar
Para desarrollo, puedes usar:

sh
npm run dev
🧪 API y Pruebas
Levantar el servidor: Ejecuta el proyecto y asegúrate de que el servidor está corriendo en http://localhost:3000 (o el puerto configurado).

Probar las APIs con Postman o cURL: Ejemplo de consulta de pacientes con cURL:

sh
curl -X GET http://localhost:3000/api/pacientes
También puedes importar la colección de Postman incluida en el repositorio para probar las diferentes rutas.

🖥️ Requisitos del Entorno
Node.js >= 14.x
Base de datos: MySQL o PostgreSQL (según configuración en .env)
Herramientas recomendadas: Postman para pruebas de API, Docker si deseas contenerizar la aplicación.
