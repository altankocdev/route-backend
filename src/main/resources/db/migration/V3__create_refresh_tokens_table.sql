CREATE TABLE refresh_tokens
(
    id             UUID         PRIMARY KEY,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,
    created_by     UUID,
    updated_by     UUID,
    version        BIGINT       NOT NULL DEFAULT 0,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    deactivated_at TIMESTAMP,

    principal_id   UUID         NOT NULL,
    principal_type VARCHAR(20)  NOT NULL,
    token_hash     VARCHAR(255) NOT NULL,
    expires_at     TIMESTAMP    NOT NULL,
    revoked_at     TIMESTAMP,

    CONSTRAINT uk_refresh_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_principal ON refresh_tokens (principal_type, principal_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);