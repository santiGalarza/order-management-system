CREATE TABLE order_status_metadata (
    status_id UUID NOT NULL REFERENCES order_statuses(id) ON DELETE CASCADE,
    key VARCHAR(255) NOT NULL,
    value VARCHAR(255),
    PRIMARY KEY (status_id, key)
);
