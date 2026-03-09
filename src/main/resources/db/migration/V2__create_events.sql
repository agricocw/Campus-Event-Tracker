CREATE TABLE events (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    category     VARCHAR(50)  NOT NULL,
    location     VARCHAR(255) NOT NULL,
    start_time   TIMESTAMPTZ  NOT NULL,
    end_time     TIMESTAMPTZ  NOT NULL,
    capacity     INTEGER,
    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
                             CHECK (status IN ('DRAFT','PUBLISHED','CANCELLED')),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE event_tags (
    event_id UUID        NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    tag      VARCHAR(50) NOT NULL,
    PRIMARY KEY (event_id, tag)
);

CREATE INDEX idx_events_start_status ON events(start_time, status);
CREATE INDEX idx_events_category     ON events(category);
CREATE INDEX idx_events_organizer    ON events(organizer_id);
