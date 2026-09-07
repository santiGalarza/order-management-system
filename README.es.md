![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Tests](https://img.shields.io/badge/Tests-JUnit5%20%7C%20Mockito%20%7C%20Testcontainers-25A162?logo=junit5&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow)

[🇬🇧 English](README.md) | [🇦🇷 Español](README.es.md)

# Sistema de Gestión de Órdenes

Backend para la gestión de productos, categorías y órdenes de compra, con autenticación JWT, autorización basada en roles y permisos, y control de propiedad de datos por usuario. Construido con Spring Boot 4 y Java 21.

## Índice

- [Funcionalidades](#funcionalidades)
- [API](#api)
- [Arquitectura y Decisiones de Diseño](#arquitectura-y-decisiones-de-diseño)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Seguridad](#seguridad)
- [Stack Tecnológico](#stack-tecnológico)
- [Requisitos](#requisitos)
- [Cómo Correrlo Localmente](#cómo-correrlo-localmente)
- [Testing](#testing)
- [Mejoras Pendientes](#mejoras-pendientes)
- [Roadmap](#roadmap)

## Funcionalidades

- Autenticación JWT (Access + Refresh Tokens)
- Rotación de Refresh Tokens con detección de reuso
- Refresh Tokens hasheados en Redis, rastreados por dispositivo
- Token Versioning: cambiar la contraseña invalida todas las sesiones activas al instante
- Control de acceso basado en roles (RBAC)
- Autorización basada en permisos
- Owner Scoping de recursos
- Gestión de Productos, Categorías y Órdenes
- Categorías jerárquicas
- Máquina de estados de órdenes basada en base de datos, con validación de transiciones y auditoría completa
- Restauración automática de stock en cancelaciones y devoluciones confirmadas
- Migraciones de base de datos con Flyway
- Entorno reproducible con Docker Compose
- Documentación OpenAPI (SpringDoc + ReDoc)
- Testeado en las capas de dominio, servicio y repositorio con JUnit 5, Mockito y Testcontainers

## ¿Por Qué Este Proyecto?

Este proyecto nació como una forma de profundizar conceptos utilizados en aplicaciones backend reales, en lugar de seguir un tutorial. Algunas decisiones salieron directamente de ese objetivo.

El estado de una orden está modelado como una máquina de estados basada en base de datos (tablas dedicadas para estados, transiciones válidas e historial) en lugar de un enum fijo. Eso mantiene la regla de negocio de "qué transiciones están permitidas" en los datos, así puede cambiar sin un deploy de código, y cada transición queda auditada: quién la hizo, cuándo, y por qué.

Del lado de seguridad, los refresh tokens rotan y se hashean antes de tocar Redis, el reuso se detecta con una ventana de gracia, y cada usuario tiene una versión de token que se incrementa al cambiar la contraseña. Esto último significa que un cambio de contraseña invalida todas las sesiones existentes al instante, en vez de esperar a que un token expire por sí solo.

El modelo de permisos y la matriz de estados de orden están ambos externalizados como datos en lugar de lógica hardcodeada. El rol admin opera dentro de todo lo que esos datos permiten hoy, en lugar de requerir cambios de código para cada escenario operativo. Cambiar la matriz en sí, como agregar un permiso o una transición de estado nueva, hoy sigue pasando por una migración de Flyway, no por un endpoint en vivo.

## Arquitectura

![Diagrama de arquitectura](docs/architecture.png)

## Arquitectura y Decisiones de Diseño

- El estado de una orden es una máquina de estados basada en base de datos (`order_statuses`, `order_status_transitions`, `order_status_history`), no un enum, así las transiciones permitidas viven en los datos y cada cambio queda registrado con quién lo hizo, cuándo, y por qué.
- El token versioning en la entidad de usuario hace que un cambio de contraseña invalide todos los refresh tokens de ese usuario al instante, en lugar de depender solo de que los tokens expiren naturalmente.
- Los permisos y las transiciones de estado de orden están modelados como datos, no como lógica hardcodeada, así las reglas que el sistema aplica viven en la base de datos, sembradas y versionadas mediante Flyway, en lugar de estar dispersas en condicionales dentro del código.
- Los refresh tokens se hashean antes de guardarse en Redis, nunca se almacenan en texto plano.
- El acceso entre usuarios distintos devuelve 404, no 403, para no confirmar la existencia de recursos ajenos.
- La autorización es basada en permisos (`@RequiresPermission`), no solo en roles, así las reglas de acceso se pueden ajustar sin tocar la lógica de negocio.
- Flyway gestiona todos los cambios de esquema, por lo que el estado de la base de datos queda versionado y reproducible.
- El código está organizado por dominio (feature based packaging) en lugar de por capa técnica, así todo lo relacionado a un concepto (`order`, `product`, `category`, `user`) vive junto.

## Seguridad

| Permiso | USER | EMPLOYEE | ADMIN |
|---------|------|----------|-------|
| ORDER_READ | ✓ | ✓ | ✓ |
| ORDER_READ_ALL | | ✓ | ✓ |
| ORDER_CREATE | ✓ | ✓ | ✓ |
| ORDER_UPDATE | ✓ | ✓ | ✓ |
| ORDER_DELETE | ✓ | ✓ | ✓ |
| PRODUCT_READ | ✓ | ✓ | ✓ |
| PRODUCT_CREATE | | ✓ | ✓ |
| PRODUCT_UPDATE | | ✓ | ✓ |
| PRODUCT_DELETE | | | ✓ |
| CATEGORY_READ | ✓ | ✓ | ✓ |
| CATEGORY_CREATE | | ✓ | ✓ |
| CATEGORY_UPDATE | | ✓ | ✓ |
| CATEGORY_DELETE | | | ✓ |
| USER_READ | ✓ | ✓ | ✓ |
| USER_READ_ALL | | | ✓ |
| USER_UPDATE | ✓ | ✓ | ✓ |
| USER_UPDATE_ALL | | | ✓ |
| USER_DELETE | | | ✓ |
| USER_ASSIGN_ROLE | | | ✓ |
| USER_SET_ROLE | | | ✓ |
| STATUS_MANAGE | | ✓ | ✓ |

- JWT access y refresh tokens
- Rotación de refresh tokens, con detección de reuso y ventana de gracia
- Refresh tokens hasheados y almacenados en Redis, rastreados por dispositivo
- Token versioning: un cambio de contraseña invalida todas las sesiones activas
- Owner scoping en el acceso y modificación de órdenes
- Autorización basada en roles y permisos

> **Nota sobre los permisos de orden:** `ORDER_UPDATE` y `ORDER_DELETE` para el rol USER
> están intencionalmente acotados. Un cliente solo puede modificar o eliminar sus
> propias órdenes, y solo mientras la orden esté en un estado modificable (PENDING).
> Una vez que la orden fue confirmada, ninguna acción del cliente puede tocarla.
> Intentar acceder a la orden de otro usuario devuelve 404, no 403, para no
> confirmar su existencia.

## Stack Tecnológico

| Categoría      | Tecnología                 |
|----------------|------------------------------|
| Lenguaje       | Java 21                     |
| Framework      | Spring Boot 4               |
| Base de datos  | PostgreSQL                  |
| Cache          | Redis                       |
| Migraciones    | Flyway                      |
| Contenedores   | Docker Compose               |
| Seguridad      | JWT                          |
| Mapeo          | MapStruct                    |
| Testing        | JUnit 5, Mockito, Testcontainers |
| Documentación  | SpringDoc OpenAPI + ReDoc     |

## API

La API está documentada con SpringDoc OpenAPI y publicada mediante ReDoc en GitHub Pages.

📖 **Documentación:** https://santigalarza.github.io/order-management-system/

### Recursos

- `/auth`
- `/users`
- `/orders`
- `/products`
- `/categories`

## Estructura del Proyecto

```
src/main/java
└── com.santiGalarza.order_management
    ├── security
    ├── user
    ├── order
    ├── product
    ├── category
    ├── common
    └── config
```

Los tests siguen la misma estructura bajo `src/test/java`, paquete por paquete.

## Requisitos

- Java 21
- Docker
- Docker Compose

## Cómo Correrlo Localmente

La aplicación está completamente containerizada.

```bash
git clone <repo-url>
cd <carpeta-del-proyecto>
cp .env.example .env   # completar secretos de DB/Redis/JWT
docker compose up
```

Esto levanta Postgres, Redis y la app (build multi stage, usuario no root en runtime). Flyway corre las migraciones automáticamente al iniciar. Los datos semilla (perfil dev) crean tres cuentas de prueba: admin, employee y customer, usadas en toda la suite de tests de la API descrita abajo.

## Testing

El proyecto está testeado en varias capas, no solo en el camino feliz:

- **Tests de dominio** cubren la lógica de las entidades de forma aislada (`Order`, `Item`, `Product`), sin necesidad de contexto de Spring.
- **Tests de utilidades de seguridad** cubren la generación, validación y expiración de JWT (`JwtUtil`), y el hasheo de refresh tokens (`TokenHasher`).
- **Tests de la capa de servicio** usan JUnit 5 y Mockito para cubrir la lógica de negocio y sus ramificaciones de forma aislada, incluyendo los flujos de autenticación, la rotación y detección de reuso de refresh tokens, y la gestión de órdenes y usuarios.
- **Tests de la capa de repositorio** corren contra una instancia real de PostgreSQL mediante Testcontainers en lugar de una base de datos en memoria, así se ejercitan tanto el comportamiento específico de Postgres como las migraciones de Flyway de la misma forma en que corren en producción.

Para correr la suite automatizada:

```bash
./mvnw test
```

Testcontainers necesita que Docker esté corriendo localmente, ya que levanta un contenedor descartable de Postgres para los tests de repositorio.

Aparte, `requests.http` (formato IntelliJ HTTP Client) cubre Auth, Usuarios, Órdenes (con ítems y transiciones de estado), Categorías y Productos de punta a punta a nivel HTTP, incluyendo casos negativos: contraseña incorrecta, sin token, recurso inexistente, transiciones de estado inválidas y límites de permisos por rol. Para correrlo: abrir `requests.http` en IntelliJ o WebStorm con el plugin HTTP Client, ejecutar primero los requests de login (encadenan el token resultante a los siguientes requests vía `client.global.set(...)`), y luego correr el resto en orden.

## Mejoras Pendientes

- Estandarizar las respuestas de error en todos los endpoints.
- Exponer el historial de estados de órdenes mediante un endpoint dedicado.
- Definir la granularidad de permisos de lectura entre cliente y empleado sobre ítems de orden.

## Roadmap

- [x] Autenticación JWT
- [x] Rotación de Refresh Tokens
- [x] Redis
- [x] Docker Compose
- [x] Flyway
- [x] Documentación de la API con SpringDoc OpenAPI y ReDoc
- [x] Tests unitarios (JUnit + Mockito)
- [ ] Tests de integración (Testcontainers, MockMvc)
- [ ] CI con GitHub Actions
- [ ] Observabilidad con Spring Boot Actuator