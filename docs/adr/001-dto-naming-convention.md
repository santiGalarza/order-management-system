# ADR 001: DTO Naming Convention

## Status
Accepted

## Context
DTOs were inconsistently named (OrderUpdateRequestDto, ItemPatchDto) making intent unclear.

## Decision
Standardize all DTOs following this convention:
- POST → Create{Entity}Request
- PUT  → Update{Entity}Request
- PATCH → Patch{Entity}Request
- GET → {Entity}Response

## Consequences
- Intent is immediately clear from class name
- Requires one time refactor of existing DTOs