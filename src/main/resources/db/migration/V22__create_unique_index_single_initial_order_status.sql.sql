-- Ensures only one order status can be flagged as the initial status at any time.
CREATE UNIQUE INDEX uq_order_statuses_single_initial
    ON order_statuses (is_initial)
    WHERE is_initial = true;