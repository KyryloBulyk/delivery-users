INSERT INTO role (name) VALUES ('ROLE_USER');
INSERT INTO role (name) VALUES ('ROLE_ADMIN');
INSERT INTO role (name) VALUES ('ROLE_RESTAURANT_ADMIN');
INSERT INTO role (name) VALUES ('ROLE_COURIER');

INSERT INTO users (username, password, email, role_id) VALUES ('user1', 'password1', 'user1@example.com', 1);
INSERT INTO users (username, password, email, role_id) VALUES ('admin', 'password2', 'admin@example.com', 2);
INSERT INTO users (username, password, email, role_id) VALUES ('restAdmin', 'password3', 'restadmin@example.com', 3);
INSERT INTO users (username, password, email, role_id) VALUES ('courier1', 'password4', 'courier1@example.com', 4);
INSERT INTO users (username, password, email, role_id) VALUES ('user2', 'password5', 'user2@example.com', 1);
