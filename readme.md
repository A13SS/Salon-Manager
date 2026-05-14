# Salon Manager

Aplicación web empresarial para la digitalización de la gestión de citas en centros de peluquería y estética.

---

## Descripción

Salon Manager sustituye los métodos tradicionales en papel por un sistema digital seguro y eficiente. La plataforma garantiza la integridad de la agenda mediante la 
validación estricta de solapes de horarios, gestiona el acceso diferenciado por roles y cumple con la normativa RGPD permitiendo al administrador el control total 
sobre los datos personales de los usuarios.

---

## Funcionalidades Principales

- **Gestión de Roles:** Acceso diferenciado para Cliente, Profesional y Administrador.
- **Reserva de Citas:** Selección de profesional, servicio y fecha con generación dinámica de huecos disponibles.
- **Validación de Solapes:** Lógica backend que impide reservas superpuestas basado en la duración real de cada servicio.
- **Gestión de Usuarios:** Listado, filtrado, cambio de rol, baja lógica y borrado físico (Admin).
- **Trazabilidad:** Sistema de logs centralizado y manejo global de excepciones con respuestas JSON estructuradas.
- **Seguridad:** Autenticación mediante JWT, encriptación de contraseñas y control de acceso basado en roles.

---

## Stack Tecnológico

| Capa | Tecnologías |
|------|-------------|
| **Frontend** | Angular · TypeScript · SCSS |
| **Backend** | Spring Boot · Spring Security + JWT · JPA / Hibernate · MySQL |
| **Despliegue** | Vercel (Frontend) · Render (Backend) · Railway (BBDD) |
| **Testing** | JUnit 5 · Mockito |

---

## Requisitos Previos

- Java 21 o superior
- Node.js (versión LTS)
- Angular CLI
- Maven
- MySQL
- Git

---

## Instalación y Ejecución

### Backend

1. Clona el repositorio:

```bash
git clone https://github.com/A13SS/Salon-Manager.git
cd Salon-Manager/Backend
```

2. Configura la conexión a base de datos en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/salon_manager
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

3. Compila y ejecuta:

```bash
mvn spring-boot:run
```

### Frontend

1. Accede al directorio del frontend:

```bash
cd Salon-Manager/Frontend
```

2. Instala dependencias y ejecuta:

```bash
npm install
npm start
```

---

## Uso

### Registro e Inicio de Sesión

Los usuarios pueden registrarse proporcionando nombre, email, teléfono y contraseña. El inicio de sesión genera un token JWT que se almacena en el navegador para 
autenticar peticiones posteriores.

### Cliente

- Crear citas seleccionando profesional, servicio y fecha, y escribiendo de manera opcional observaciones y alergias.
- Visualizar el historial de citas propias.
- Cancelar citas pendientes.

### Profesional

- Visualizar citas asignadas con filtro por fecha.
- Confirmar, cancelar o marcar citas como atendidas.
- Gestionar su agenda y disponibilidad.
- Crear citas.
- Acceso a la vista de servicios.

### Administrador

- Gestión completa de usuarios: cambio de rol, baja lógica y eliminación permanente.
- Listado y gestión de servicios ofertados.

---

## Endpoints Principales

| Método   | Ruta                            | Descripción                          |
|----------|---------------------------------|--------------------------------------|
| `POST`   | `/api/auth/registro`            | Registrar nuevo usuario              |
| `POST`   | `/api/auth/login`               | Autenticar usuario y obtener JWT     |
| `GET`    | `/api/citas/cliente/{id}`       | Listar citas de un cliente           |
| `GET`    | `/api/citas/profesional/{id}`   | Listar citas de un profesional       |
| `POST`   | `/api/citas/crear`              | Crear nueva cita                     |
| `GET`    | `/api/citas/huecos-disponibles` | Obtener huecos disponibles           |
| `PUT`    | `/api/citas/{id}/confirmar`     | Confirmar cita                       |
| `PUT`    | `/api/citas/{id}/cancelar`      | Cancelar cita                        |
| `PUT`    | `/api/citas/{id}/atendida`      | Marcar cita como atendida            |
| `GET`    | `/api/usuarios`                 | Listar todos los usuarios (Admin)    |
| `PATCH`  | `/api/usuarios/{id}`            | Borrado lógico de un usuario (Admin) |
| `DELETE` | `/api/usuarios/{id}`            | Eliminar usuario (Admin)             |
| `GET`    | `/api/servicios`                | Listar servicios                     |
| `GET`    | `/api/servicios/activos`        | Listar servicios activos             |

---

## Testing

El proyecto incluye **113 tests unitarios** desarrollados con JUnit 5 y Mockito que cubren:

- **Capa Domain** — validaciones de entidades y reglas de negocio.
- **Capa Application** — servicios, autenticación y lógica de citas.
- **Capa Infrastructure** — controladores y repositorios.

Para ejecutar los tests:

```bash
mvn test
```

---

## Seguridad

- Contraseñas encriptadas con **BCrypt**.
- Tokens **JWT** con expiración configurable.
- Control de acceso basado en roles: `CLIENTE`, `PROFESIONAL`, `ADMIN`.
- **CORS** configurado para dominios autorizados.
- Manejo centralizado de excepciones con `GlobalExceptionHandler`.
- Validación de solapes a nivel de servicio para garantizar la integridad de los datos.

---

## Copias de Seguridad

El sistema incluye scripts automatizados para backup y restauración:

- Backup diario mediante `backup.bat`.
- Almacenamiento en `/backups/salon-manager/` con compresión `.sql.gz`.
- Retención automática de copias durante **30 días**.
- Restauración manual mediante `restore.bat`.
- Tiempo estimado de recuperación ante desastre: **15-20 minutos**.

---

## Autor

Desarrollado por **Alejandro Fernández Escribano**  
Proyecto Intermodular — Mayo 2026