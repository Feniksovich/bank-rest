--liquibase formatted sql

--changeset feniksovich:004-insert-admin-user
INSERT INTO users (id, phone_number, password, first_name, last_name, role, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           '9009990000',
           '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8Kz8Kz8K', -- admin123
           'User',
           'Admin',
           'ADMIN',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
);

--rollback DELETE FROM users WHERE phone_number = '9009990000';