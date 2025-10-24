--liquibase formatted sql

--changeset feniksovich:001-create-users-table
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users'
CREATE TABLE users (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(20)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--rollback DROP TABLE IF EXISTS users;

--changeset feniksovich:001a-create-users-idx-phone
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users'
--precondition-sql-check expectedResult:0 SELECT CASE WHEN to_regclass('public.idx_users_phone_number') IS NULL THEN 0 ELSE 1 END
CREATE INDEX idx_users_phone_number ON users(phone_number);
--rollback DROP INDEX IF EXISTS idx_users_phone_number;

--changeset feniksovich:002-create-cards-table
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'cards'
CREATE TABLE cards (
   id            UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
   pan_encrypted VARCHAR(512)    NOT NULL,
   pan_last_4    VARCHAR(4)      NOT NULL,
   card_holder   VARCHAR(100)    NOT NULL,
   expires_at    DATE            NOT NULL,
   balance       DECIMAL(19,2)   NOT NULL DEFAULT 0.00,
   blocked       BOOLEAN         NOT NULL DEFAULT FALSE,
   user_id       UUID            NOT NULL,
   CONSTRAINT fk_cards_user      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
   CONSTRAINT uq_user_pan_last_4 UNIQUE (user_id, pan_last_4)
);
--rollback DROP TABLE IF EXISTS cards;

--changeset feniksovich:002a-create-cards-idx-last4
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'cards'
--precondition-sql-check expectedResult:0 SELECT CASE WHEN to_regclass('public.idx_cards_last4') IS NULL THEN 0 ELSE 1 END
CREATE INDEX idx_cards_last4 ON cards(pan_last_4);
--rollback DROP INDEX IF EXISTS idx_cards_last4;

--changeset feniksovich:002b-create-cards-idx-user-last4
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'cards'
--precondition-sql-check expectedResult:0 SELECT CASE WHEN to_regclass('public.idx_cards_user_last4') IS NULL THEN 0 ELSE 1 END
CREATE INDEX idx_cards_user_last4 ON cards(user_id, pan_last_4);
--rollback DROP INDEX IF EXISTS idx_cards_user_last4;

--changeset feniksovich:003-create-refresh-tokens-table
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'refresh_tokens'
CREATE TABLE refresh_tokens (
    id         UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID      NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS refresh_tokens;