INSERT INTO users (id, email, password, first_name, last_name, is_active, token_version) VALUES
('94c2821d-a8d3-4e9d-b808-3d040a04bec7', 'admin@example.com', '${bcryptHash}', 'Ada', 'Admin', true, 0),
('2734cce5-5ec4-4fc1-9de5-f58ec323a84a', 'employee@example.com', '${bcryptHash}', 'Emma', 'Employee', true, 0),
('39851b38-d96b-4ee4-88ef-49981724666a', 'customer@example.com', '${bcryptHash}', 'Chris', 'Customer', true, 0);
