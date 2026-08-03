CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    price NUMERIC(8,2) NOT NULL,
    sku VARCHAR(12) NOT NULL UNIQUE,
    min_order_quantity INTEGER NOT NULL,
    stock_quantity INTEGER NOT NULL,
    weight REAL NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    is_active BOOLEAN NOT NULL DEFAULT true
);
