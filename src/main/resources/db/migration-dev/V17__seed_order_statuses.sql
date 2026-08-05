INSERT INTO order_statuses (id, code, label, is_initial, is_final, is_modifiable) VALUES
('cf79fd06-e988-406d-b628-8aa999aa0ea1', 'PENDING', 'Pending', true, false, true),
('a062594c-549c-45da-998f-c20a6158b4ec', 'CONFIRMED', 'Confirmed', false, false, false),
('b33b1319-26f4-4b64-a441-116e6efaa4e3', 'SHIPPED', 'Shipped', false, false, false),
('3f2f95a2-d3d5-4211-a1b2-9f2a1ba5cea2', 'DELIVERED', 'Delivered', false, false, false),
('8ba122bf-0f90-4800-b68c-a466f76ff794', 'RETURN_REQUESTED', 'Return Requested', false, false, false),
('0c0fbefa-5acc-476c-af69-6bed18b0ebc6', 'RETURN_CONFIRMED', 'Return Confirmed', false, true, false),
('41a4bc1c-f9ba-4832-b482-6a90504910e1', 'DELIVERY_FAILED', 'Delivery Failed', false, false, false),
('102663f8-4228-484c-8bec-18ae21197850', 'REATTEMPTING_DELIVERY', 'Reattempting Delivery', false, false, false),
('d5a4adfb-e15d-40d8-92d4-18bb48495edf', 'CANCELLED', 'Cancelled', false, true, false);
