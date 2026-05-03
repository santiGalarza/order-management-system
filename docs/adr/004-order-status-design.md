# ADR 004: Order Status Design

## Status
Accepted

## Context
Order statuses needed to drive business logic such as preventing modifications to shipped orders. The two main 
approaches were a Java enum or a database table.

## Decision
Implement OrderStatus as a Java enum.

## Alternatives Considered
- Database table with flags: rejected because order statuses drive hardcoded application logic. Adding a new status to 
the DB without updating the code would have no effect and could introduce inconsistencies.

## Consequences
- Type safety and compile-time verification for status handling
- New statuses require a code change, which is correct because new statuses always require corresponding business logic
updates
- Easier to maintain and understand than a dynamic table-based approach
