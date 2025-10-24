--liquibase formatted sql

--changeset feniksovich:004-insert-admin-user
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users'
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users WHERE phone_number = '9009990000'
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