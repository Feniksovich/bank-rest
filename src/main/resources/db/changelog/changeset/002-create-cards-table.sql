--liquibase formatted sql

--changeset feniksovich:002-create-cards-table
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

CREATE INDEX idx_cards_last4 ON cards(pan_last_4);
CREATE INDEX idx_cards_user_last4 ON cards(user_id, pan_last_4);

--rollback DROP INDEX IF EXISTS idx_cards_user_last4;
--rollback DROP INDEX IF EXISTS idx_cards_last4;
