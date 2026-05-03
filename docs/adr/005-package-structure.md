# ADR 005: Package Structure

## Status
Accepted

## Context
Two common approaches exist for organizing code: package by layer and package by feature. The project needed a clear 
convention before growing further.

## Decision
Package by feature with internal layer separation.

com.santiGalarza.order_management

- order/
- product/
- category/
- product/
- client/
- auth

Each feature package contains its own internal layers:

order/
├── controller/
├── service/
├── repository/
├── domain/
├── dto/

Each feature owns all logic related to its domain, including controllers, services, repositories, entities, and DTOs.

Cross-cutting concerns that do not belong to any specific domain are placed in `common`, such as:
- exception handling
- configuration
- shared utilities

`common` must not contain business logic or domain-specific behavior.
DTO naming follows ADR-001.

## Alternatives Considered
- Package by layer (controller/, service/, repository/ at the top level): rejected because it scatters related classes
across the codebase. Understanding one feature requires navigating multiple packages.

## Consequences
- All code related to a feature is easy to navigate
- New features are added by creating a new top level package, not touching existing ones
- `common` has a clear contract 