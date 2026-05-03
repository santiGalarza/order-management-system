# ADR 002: Error Handling Strategy

## Status
Accepted

## Context
The application needed a consistent, standards-compliant way to return error responses across all endpoints. Initial 
approach used a custom ErrorResponseDto with just a message and status code, which lacked context and was not aligned
with any standard.

## Decision
Adopt RFC 9457 (Problem Details for HTTP APIs) using Spring Boot 3's built-in
ProblemDetail class. A single GlobalExceptionHandler annotated with
@RestControllerAdvice handles all exceptions and returns a consistent response:

- type: defaults to about:blank
- title: short human-readable error category
- status: HTTP status code
- detail: specific message for this occurrence
- instance: the request URI that triggered the error

All {Entity} not found exceptions extend a base ResourceNotFoundException which is caught by a single handler, avoiding 
one handler per entity.

Custom exceptions:
- ResourceNotFoundException (base) → OrderNotFoundException,
  ItemNotFoundException, ProductNotFoundException, CategoryNotFoundException
- InsufficientStockException for business rule violations (HTTP 409 Conflict)

## Alternatives Considered
- Custom ErrorResponseDto: rejected for being non-standard and requiring more
  maintenance.
- One exception handler per entity: rejected for duplication.

## Consequences
- Any API consumer familiar with RFC 9457 understands the error format immediately
- Adding new exceptions requires minimal boilerplate
- Auth exceptions (AccessDeniedException, AuthenticationException) will require a custom AuthenticationEntryPoint and
AccessDeniedHandler when Spring Security is added, as those are intercepted before reaching @RestControllerAdvice. These
are noted here for future implementation as current project does not include authentication.