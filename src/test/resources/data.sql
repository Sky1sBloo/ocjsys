--INSERT INTO roles (name) VALUES ('ROLE_USER');
--INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (username, password) VALUES ('root', '$2a$10$QksqAgf85I55MRTBnqpsxOTuRygPhLI.ZBI8mh5w0don1FN4xVkS.');
-- INSERT INTO users (username, password) VALUES ('root', '1234');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);