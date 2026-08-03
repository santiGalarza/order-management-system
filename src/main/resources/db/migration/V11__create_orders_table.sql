CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_price NUMERIC(19,2),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    current_status_id UUID NOT NULL REFERENCES order_statuses(id),
    user_id UUID NOT NULL REFERENCES users(id),
    delivery_attempts INTEGER NOT NULL DEFAULT 0
);
