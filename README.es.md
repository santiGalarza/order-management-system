[🇬🇧 English](README.md) | [🇦🇷 Español](README.es.md)

# Sistema de Gestión de Órdenes

Backend para la gestión de productos, categorías y órdenes de compra, con autenticación JWT, autorización basada en roles/permisos y control de propiedad de datos por usuario. Construido con Spring Boot 4 y Java 21.

## Funcionalidades

- **Autenticación**: JWT con access + refresh tokens hasheados, almacenados en Redis (por dispositivo, con detección de reuso de refresh token).
- **Autorización**: basada en roles (`USER` / `EMPLOYEE` / `ADMIN`) con control de acceso granular por permisos (`@RequiresPermission`).
- **Scoping por propietario**: los clientes solo pueden leer/modificar sus propias órdenes; el personal (con `ORDER_READ_ALL`) puede acceder a cualquier orden. El acceso entre usuarios distintos devuelve `404`, no `403`, para no revelar la existencia de recursos ajenos.
- **Dominio**: Usuarios, Órdenes (con ítems, estado e historial de estados), Productos, Categorías (con jerarquía padre/hijo).
- **CRUD completo** sobre Órdenes, Productos y Categorías, con límites de permisos según el rol (por ejemplo: `READ` abierto a todos, `CREATE`/`UPDATE` para personal, `DELETE` restringido a administradores).

## Stack tecnológico

Java 21 · Spring Boot 4 · PostgreSQL 16 · Redis 7 · Flyway · Docker Compose · JWT · MapStruct · Lombok

## Cómo correrlo localmente

Todo está containerizado.

```bash
git clone <repo-url>
cd <carpeta-del-proyecto>
cp .env.example .env   # completar secretos de DB/Redis/JWT
docker compose up
```

Esto levanta Postgres, Redis y la app (build multi-stage, usuario no-root en runtime). Flyway corre las migraciones automáticamente al iniciar. Los datos semilla (perfil dev) crean tres cuentas de prueba: admin, employee y customer, usadas en toda la suite de tests de la API descrita abajo.

## Cómo probar la API

El archivo `requests.http` (formato IntelliJ HTTP Client) cubre Auth, Usuarios, Órdenes (+ ítems + transiciones de estado), Categorías y Productos de punta a punta, incluyendo casos negativos: contraseña incorrecta, sin token, recurso inexistente, transiciones de estado inválidas y límites de permisos por rol.

Para correrlo: abrir `requests.http` en IntelliJ/WebStorm con el plugin HTTP Client, ejecutar primero los requests de login (encadenan el token resultante a los siguientes requests vía `client.global.set(...)`), y luego correr el resto en orden.

## Limitaciones conocidas

- `OrderStatusHistory` todavía no tiene DTO/endpoint expuesto (el método de servicio existe pero no está conectado).
- Los mensajes de excepción actualmente devuelven IDs/emails crudos en respuestas 404 — limpieza planeada.

## Próximos pasos

- Recortar mensajes de excepción.
- Limpieza de estructura de carpetas.
- Cobertura de tests con JUnit/Mockito.