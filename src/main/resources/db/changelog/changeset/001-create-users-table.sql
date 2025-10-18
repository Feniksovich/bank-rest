--liquibase formatted sql

--changeset feniksovich:001-create-users-table
CREATE TABLE users (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(20)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE IF EXISTS users;

CREATE INDEX idx_users_phone_number ON users(phone_number);

--rollback DROP INDEX IF EXISTS idx_users_phone_number;

