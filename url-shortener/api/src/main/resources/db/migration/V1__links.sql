CREATE SEQUENCE link_code_seq START WITH 56800235584;

CREATE TABLE links (
    code varchar(32) PRIMARY KEY,
    long_url text NOT NULL CHECK (length(long_url) <= 2048),
    created_at timestamptz NOT NULL DEFAULT now(),
    expires_at timestamptz,
    deleted_at timestamptz
);
