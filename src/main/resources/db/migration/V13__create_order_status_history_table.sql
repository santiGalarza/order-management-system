CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    from_status_id UUID REFERENCES order_statuses(id),
    to_status_id UUID NOT NULL REFERENCES order_statuses(id),
    changed_by UUID,
    notes VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
