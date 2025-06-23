# Plan de Pruebas Diseñado y Herramientas Utilizadas

## 1. Objetivo del Plan de Pruebas

El objetivo principal de este plan de pruebas es evaluar el desempeño, la robustez y la capacidad de respuesta del sistema backend a través de pruebas de carga y funcionales utilizando Apache JMeter. Se busca identificar posibles cuellos de botella, errores en la lógica de negocio (por ejemplo, registro y login de usuarios) y comprobar el manejo adecuado de múltiples solicitudes concurrentes.

---

## 2. Herramientas Utilizadas

### 2.1 Apache JMeter
- **Descripción:** Herramienta open source para pruebas de carga, rendimiento y funcionales de aplicaciones web.
- **Uso en el proyecto:** 
  - Simulación de múltiples usuarios concurrentes.
  - Pruebas de los endpoints `/api/register` y `/api/login`.
  - Análisis de tiempos de respuesta, tasas de error y comportamiento bajo diversas cargas.
  - Generación de reportes gráficos y tablas resumen.

### 2.2 Base de datos relacional (ej: MySQL, PostgreSQL)
- **Descripción:** Motor de base de datos utilizado para almacenar los registros de usuarios.
- **Uso en el proyecto:**
  - Verificación de creación y existencia de usuarios tras las pruebas.
  - Manipulación de registros para limpieza y reinicio de escenarios de prueba.

### 2.3 Herramientas complementarias
- **Editor de SQL (ej: DBeaver, MySQL Workbench):**
  - Para consultar, modificar o limpiar la tabla de usuarios.
- **Generadores de hash bcrypt (opcional):**
  - Para generar contraseñas encriptadas al modificar manualmente la base de datos.

---

## 3. Diseño del Plan de Pruebas

### 3.1 Casos de prueba principales

#### 3.1.1 Prueba de Registro de Usuario (`/api/register`)
- **Propósito:** Validar la creación masiva de usuarios y la correcta respuesta del sistema ante datos únicos y repetidos.
- **Parámetros configurados en JMeter:**
  - Número de usuarios (hilos): 20-50
  - Datos de usuario dinámicos (email único por hilo)
  - Verificación de respuestas exitosas y manejo de errores (email ya existente)

#### 3.1.2 Prueba de Login de Usuario (`/api/login`)
- **Propósito:** Verificar la autenticación correcta de usuarios existentes bajo carga concurrente.
- **Parámetros configurados:**
  - Múltiples usuarios registrados previamente
  - Contraseñas en texto plano (el backend compara contra el hash en la base)
  - Validación de tokens o mensajes de éxito/fracaso

#### 3.1.3 Pruebas de Resistencia y Comportamiento ante Errores
- **Propósito:** Evaluar la respuesta del sistema ante intentos de login o registro con datos erróneos o repetidos.
- **Escenarios:**
  - Login con contraseña incorrecta.
  - Registro con email ya existente.
  - Solicitudes mal formateadas.

---

## 4. Configuración de JMeter

- **Elementos utilizados:**
  - Thread Group (Grupo de Hilos)
  - HTTP Request (Petición HTTP)
  - HTTP Header Manager (Gestor de Cabeceras HTTP)
  - View Results Tree (Árbol de Resultados)
  - Summary Report (Informe Resumido)
- **Variables dinámicas:** Uso de funciones `${__threadNum}` para generar datos únicos.
- **Headers:** `Content-Type: application/json`
- **Body Data:** Estructura JSON acorde al endpoint probado.

---

## 5. Criterios de Éxito

- Al menos el 95% de las solicitudes deben ser exitosas bajo la carga esperada.
- Los usuarios deben ser creados y autenticados correctamente.
- Los tiempos de respuesta deben estar dentro de los límites aceptables según los requisitos del sistema.
- Los errores deben ser manejados y reportados adecuadamente por el sistema.

---

## 6. Limpieza y Reinicio de Escenarios

- Limpieza de usuarios creados en la base de datos mediante consultas SQL para repetir las pruebas con los mismos datos.
- Alternativamente, generación de nuevos usuarios con datos únicos en cada ejecución.

---

**Autor:**  
Brandonttt  
**Fecha:** 2025-06-23
