INSERT INTO orders (id, total_price, current_status_id, user_id, delivery_attempts) VALUES
('2d5617c3-981f-443d-af02-bd401f3d810d', 44.48, 'cf79fd06-e988-406d-b628-8aa999aa0ea1', '39851b38-d96b-4ee4-88ef-49981724666a', 0);

INSERT INTO order_items (product_id, order_id, unit_price, quantity) VALUES
('a1bcc21d-cc6d-47a1-8e89-ba7830cd3014', '2d5617c3-981f-443d-af02-bd401f3d810d', 10.99, 2),
('671c377f-c389-44ed-9a97-e857d0400fde', '2d5617c3-981f-443d-af02-bd401f3d810d', 7.50, 3);

INSERT INTO order_status_history (order_id, from_status_id, to_status_id, changed_by, notes) VALUES
('2d5617c3-981f-443d-af02-bd401f3d810d', NULL, 'cf79fd06-e988-406d-b628-8aa999aa0ea1', '39851b38-d96b-4ee4-88ef-49981724666a', 'Order created');
