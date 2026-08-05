CREATE TABLE order_status_transitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_status_id UUID NOT NULL REFERENCES order_statuses(id),
    to_status_id UUID NOT NULL REFERENCES order_statuses(id),
    requires_role VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (from_status_id, to_status_id)
);
