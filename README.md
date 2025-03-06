# Sistema Médico

Este es un sistema médico diseñado para gestionar información de pacientes, consultas y personal de salud. Proporciona funcionalidades para el manejo eficiente de los datos médicos, facilitando la administración y consulta de registros clínicos.

## 📌 Características principales
- **Gestión de pacientes:** Registro, actualización y eliminación de datos.
- **Administración de consultas:** Agendamiento y seguimiento de citas médicas.
- **Manejo de personal médico:** Registro y gestión de información de doctores y enfermeros.
- **APIs RESTful:** Para interactuar con el sistema de manera programática.

## 🛠️ Instalación y configuración
### 1️⃣ Clonar el repositorio
```sh
 git clone https://github.com/Brandonttt/SistemaMedico.git
 cd SistemaMedico
```

### 2️⃣ Instalar dependencias
Asegúrate de tener instalado [Node.js](https://nodejs.org/) y [npm](https://www.npmjs.com/) o [yarn](https://yarnpkg.com/).

```sh
npm install  # o yarn install
```

### 3️⃣ Configurar variables de entorno
Renombra el archivo `.env.example` a `.env` y configura las credenciales necesarias como la conexión a la base de datos.

### 4️⃣ Compilar y ejecutar el proyecto
```sh
npm run build  # Compilar
npm start      # Ejecutar
```
Para desarrollo, puedes usar:
```sh
npm run dev
```

## 📡 API y pruebas
### 1️⃣ Levantar el servidor
Ejecuta el proyecto y asegúrate de que el servidor está corriendo en `http://localhost:3000` (o el puerto configurado).

### 2️⃣ Probar las APIs con Postman o cURL
Ejemplo de consulta de pacientes con cURL:
```sh
curl -X GET http://localhost:3000/api/pacientes
```

También puedes importar la colección de Postman incluida en el repositorio para probar las diferentes rutas.

## 📷 Capturas de pruebas con Postman
A continuación, se deben agregar capturas de pantalla de los endpoints funcionales, como registro y login de usuarios, utilizando Postman o navegadores para solicitudes HTTP exitosas.
![image](https://github.com/user-attachments/assets/5ada42d4-d123-4b21-aee9-30a5a942d621)


_🔹 Agrega aquí las capturas de pantalla de las pruebas realizadas._

## 📋 Requisitos del entorno
- **Node.js** >= 14.x
- **Base de datos**: MySQL o PostgreSQL (según configuración en `.env`)
- **Herramientas recomendadas**: Postman para pruebas de API, Docker si deseas contenerizar la aplicación.

## 🚀 Contribución
Si deseas contribuir al proyecto, ¡eres bienvenido! Puedes enviar un *pull request* con mejoras o correcciones.

## 📄 Licencia
Este proyecto está bajo la licencia MIT. Puedes consultar más detalles en el archivo `LICENSE`.

---

📌 *Si tienes dudas o sugerencias, abre un issue en el repositorio.*
