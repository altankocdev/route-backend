-- ============================================================
-- V2: user_activity_logs + admin_audit_logs -> tek activity_logs tablosu
-- ============================================================

DROP TABLE IF EXISTS user_activity_logs;
DROP TABLE IF EXISTS admin_audit_logs;

-- activity_logs: tek, merkezi olay akışı — hem kullanıcı hem admin hem sistem olayları burada.
-- actor_id gerçek bir FK DEĞİL (hem users.id hem admins.id'ye işaret edebiliyor, polimorfik) —
-- aynı sebeple target_id de FK değil (Report.target_id ile aynı tasarım prensibi).
CREATE TABLE activity_logs
(
    id             UUID        PRIMARY KEY,
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP   NOT NULL,
    created_by     UUID,
    updated_by     UUID,
    version        BIGINT      NOT NULL DEFAULT 0,
    active         BOOLEAN     NOT NULL DEFAULT TRUE,
    deactivated_at TIMESTAMP,

    actor_id       UUID,
    actor_type     VARCHAR(20) NOT NULL,
    event_type     VARCHAR(40) NOT NULL,
    target_type    VARCHAR(20) NOT NULL,
    target_id      UUID        NOT NULL,
    detail         VARCHAR(500)
);

CREATE INDEX idx_activity_logs_actor ON activity_logs (actor_type, actor_id);
CREATE INDEX idx_activity_logs_target ON activity_logs (target_type, target_id);
CREATE INDEX idx_activity_logs_created_at ON activity_logs (created_at);