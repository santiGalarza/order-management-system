CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    unit_price NUMERIC(8,2) NOT NULL,
    quantity INTEGER NOT NULL
);
