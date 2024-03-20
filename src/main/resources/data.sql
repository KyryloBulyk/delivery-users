INSERT INTO role (role_id, name) VALUES (1, 'ROLE_USER');
INSERT INTO role (role_id, name) VALUES (2, 'ROLE_ADMIN');
INSERT INTO role (role_id, name) VALUES (3, 'ROLE_RESTAURANT_ADMIN');
INSERT INTO role (role_id, name) VALUES (4, 'ROLE_COURIER');

INSERT INTO users (user_id, username, password, email, role_id) VALUES (1, 'user1', 'password1', 'user1@example.com', 1);
INSERT INTO users (user_id, username, password, email, role_id) VALUES (2, 'admin', 'password2', 'admin@example.com', 2);
INSERT INTO users (user_id, username, password, email, role_id) VALUES (3, 'restAdmin', 'password3', 'restadmin@example.com', 3);
INSERT INTO users (user_id, username, password, email, role_id) VALUES (4, 'courier1', 'password4', 'courier1@example.com', 4);
INSERT INTO users (user_id, username, password, email, role_id) VALUES (5, 'user2', 'password5', 'user2@example.com', 1);
