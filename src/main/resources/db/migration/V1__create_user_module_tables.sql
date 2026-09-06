-- ============================================================
-- V1: User modülü tabloları (users, user_tokens, user_activity_logs, admin_audit_logs)
-- ============================================================

-- ------------------------------------------------------------
-- users
-- ------------------------------------------------------------
CREATE TABLE users
(
    id                    UUID PRIMARY KEY,
    created_at            TIMESTAMP    NOT NULL,
    updated_at            TIMESTAMP    NOT NULL,
    created_by            UUID,
    updated_by            UUID,
    version               BIGINT       NOT NULL DEFAULT 0,
    active                BOOLEAN      NOT NULL DEFAULT TRUE,
    deactivated_at        TIMESTAMP,

    first_name            VARCHAR(100) NOT NULL,
    last_name             VARCHAR(100) NOT NULL,
    username              VARCHAR(30)  NOT NULL,
    email                 VARCHAR(150) NOT NULL,
    password_hash         VARCHAR(255),
    google_id             VARCHAR(255),
    profile_photo_url     VARCHAR(500),

    role                  VARCHAR(20)  NOT NULL,
    status                VARCHAR(20)  NOT NULL,

    ban_reason            VARCHAR(500),
    banned_at             TIMESTAMP,

    email_verified        BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login_at         TIMESTAMP,
    terms_accepted_at     TIMESTAMP,
    deletion_requested_at TIMESTAMP,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_google_id UNIQUE (google_id),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_users_role ON users (role);

-- ------------------------------------------------------------
-- user_tokens (email doğrulama / email değişikliği / şifre sıfırlama)
-- ------------------------------------------------------------
CREATE TABLE user_tokens
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,
    created_by     UUID,
    updated_by     UUID,
    version        BIGINT       NOT NULL DEFAULT 0,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    deactivated_at TIMESTAMP,

    user_id        UUID         NOT NULL,
    token          VARCHAR(255) NOT NULL,
    token_type     VARCHAR(30)  NOT NULL,
    new_email      VARCHAR(150),
    expires_at     TIMESTAMP    NOT NULL,
    used_at        TIMESTAMP,

    CONSTRAINT uk_user_tokens_token FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_user_tokens_token UNIQUE (token)
);

CREATE INDEX idx_user_tokens_user_id ON user_tokens (user_id);
CREATE INDEX idx_user_tokens_expires_at ON user_tokens (expires_at);

-- ------------------------------------------------------------
-- user_activity_logs (UC-28: kullanıcının kendi hareket geçmişi)
-- ------------------------------------------------------------
CREATE TABLE user_activity_logs
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP   NOT NULL,
    created_by     UUID,
    updated_by     UUID,
    version        BIGINT      NOT NULL DEFAULT 0,
    active         BOOLEAN     NOT NULL DEFAULT TRUE,
    deactivated_at TIMESTAMP,

    user_id        UUID        NOT NULL,
    activity_type  VARCHAR(30) NOT NULL,
    target_type    VARCHAR(20) NOT NULL,
    target_id      UUID        NOT NULL,

    CONSTRAINT fk_user_activity_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_user_activity_logs_user_id ON user_activity_logs (user_id);
CREATE INDEX idx_user_activity_logs_created_at ON user_activity_logs (created_at);

-- ------------------------------------------------------------
-- admin_audit_logs (admin işlemlerinin kalıcı, silinemez kaydı)
-- ------------------------------------------------------------
CREATE TABLE admin_audit_logs
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP   NOT NULL,
    created_by     UUID,
    updated_by     UUID,
    version        BIGINT      NOT NULL DEFAULT 0,
    active         BOOLEAN     NOT NULL DEFAULT TRUE,
    deactivated_at TIMESTAMP,

    admin_id       UUID        NOT NULL,
    action_type    VARCHAR(30) NOT NULL,
    target_type    VARCHAR(20) NOT NULL,
    target_id      UUID        NOT NULL,
    reason         VARCHAR(500),

    CONSTRAINT fk_admin_audit_logs_admin FOREIGN KEY (admin_id) REFERENCES users (id)
);

CREATE INDEX idx_admin_audit_logs_admin_id ON admin_audit_logs (admin_id);
CREATE INDEX idx_admin_audit_logs_target ON admin_audit_logs (target_type, target_id);
CREATE INDEX idx_admin_audit_logs_created_at ON admin_audit_logs (created_at);