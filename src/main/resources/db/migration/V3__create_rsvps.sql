CREATE TABLE rsvps (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id   UUID        NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    status     VARCHAR(20) NOT NULL DEFAULT 'ATTENDING'
                           CHECK (status IN ('ATTENDING','WAITLIST','CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, event_id)
);

CREATE INDEX idx_rsvps_event_status ON rsvps(event_id, status);
CREATE INDEX idx_rsvps_user         ON rsvps(user_id);
