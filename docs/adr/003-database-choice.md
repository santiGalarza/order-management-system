# ADR 003: Database Choice

## Status
Accepted

## Context
As the project scope settled on order management, two database approaches were considered: PostgreSQL for its relational 
guarantees and MongoDB for document flexibility.

## Decision
PostgreSQL.

Order management is fundamentally relational — orders reference items, items reference products, all with strict
integrity requirements. PostgreSQL provides:
- Foreign key constraints guaranteeing referential integrity
- ACID transactions ensuring stock deductions and order creation are atomic
- A fixed schema that matches the structured and predictable nature of orders

## Alternatives Considered
- MongoDB: rejected because the document model adds flexibility this domain does not need. Order structure is 
predictable and unlikely to change dynamically. The loss of joins, transactions and foreign keys would be a net 
negative.

## Consequences
- Data integrity enforced at the database level, not just the application level
- ACID transactions make stock deduction and order creation safe under concurrency
- Schema migrations required when the model changes