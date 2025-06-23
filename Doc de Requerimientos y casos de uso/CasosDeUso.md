```mermaid
%% Diagrama de Casos de Uso Completo para SistemaMedico

%% Actores
actor Paciente
actor Doctor
actor Administrador
actor Recepcionista

%% Casos de uso Paciente
Paciente -- (Registrarse)
Paciente -- (Iniciar sesión)
Paciente -- (Recuperar contraseña)
Paciente -- (Solicitar cita médica)
Paciente -- (Cancelar cita)
Paciente -- (Consultar citas)
Paciente -- (Consultar expediente clínico)
Paciente -- (Recibir notificaciones de cita)
Paciente -- (Chatear con Doctor)

%% Casos de uso Doctor
Doctor -- (Iniciar sesión)
Doctor -- (Consultar agenda de citas)
Doctor -- (Registrar consulta médica)
Doctor -- (Actualizar expediente clínico)
Doctor -- (Consultar expedientes de pacientes)
Doctor -- (Gestionar próximas citas)
Doctor -- (Cancelar consulta)
Doctor -- (Chatear con Paciente)

%% Casos de uso Administrador
Administrador -- (Iniciar sesión)
Administrador -- (Registrar usuario)
Administrador -- (Registrar Doctor)
Administrador -- (Registrar Paciente)
Administrador -- (Registrar Recepcionista)
Administrador -- (Gestionar usuarios)
Administrador -- (Gestionar roles y permisos)
Administrador -- (Consultar reportes estadísticos)
Administrador -- (Administrar base de datos)
Administrador -- (Ver logs del sistema)

%% Casos de uso Recepcionista
Recepcionista -- (Iniciar sesión)
Recepcionista -- (Registrar paciente)
Recepcionista -- (Agendar cita para paciente)
Recepcionista -- (Cancelar cita)
Recepcionista -- (Consultar agenda de citas)
Recepcionista -- (Consultar pacientes)

%% Relaciones entre casos de uso
(Solicitar cita médica) ..> (Validar disponibilidad de Doctor) : include
(Registrar consulta médica) ..> (Actualizar expediente clínico) : include
(Registrar consulta médica) ..> (Generar próxima cita) : include
(Registrar usuario) ..> (Asignar rol) : include

%% Extensiones opcionales
(Paciente) ..> (Actualizar datos personales) : <<extend>>
```
