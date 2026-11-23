package dev.lab.api.link;

import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LinkRepository {

    private final JdbcClient jdbcClient;

    public LinkRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public long nextId() {
        Long id = jdbcClient
                .sql("select nextval('link_code_seq')")
                .query(Long.class)
                .single();
        return id;
    }

    public Link insert(String code, String longUrl, Instant expiresAt) {

        return jdbcClient
                .sql("""
                    INSERT INTO links (code, long_url, expires_at) 
                    VALUES (:code, :long_url, :expires_at)
                    RETURNING code, long_url, created_at, expires_at, deleted_at
                    """)
                .param(Constants.CODE_COLUMN, code)
                .param(Constants.LONG_URL_COLUMN, longUrl)
                .param(Constants.EXPIRES_AT_COLUMN, toOffsetDateTime(expiresAt))
                .query(getLinkRowMapper())
                .single();
    }

    public Optional<Link> findByCode(String code) {
        return jdbcClient.sql("""
            SELECT code, long_url, created_at, expires_at, deleted_at
            FROM links
            WHERE code = :code
            """)
                .param(Constants.CODE_COLUMN, code)
                .query(getLinkRowMapper()).optional();
    }

    private @NonNull RowMapper<Link> getLinkRowMapper() {
        return (rs, rowNum) -> new Link(
                rs.getString(Constants.CODE_COLUMN),
                rs.getString(Constants.LONG_URL_COLUMN),
                toInstant(rs.getObject(Constants.CREATED_AT_COLUMN, OffsetDateTime.class)),
                toInstant(rs.getObject(Constants.EXPIRES_AT_COLUMN, OffsetDateTime.class)),
                toInstant(rs.getObject(Constants.DELETED_AT_COLUMN, OffsetDateTime.class))
        );
    }

    private Instant toInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    @Transactional
    public DeleteResult markDeleted(String code) {
        Optional<Link> maybeLink = jdbcClient.sql("""
            SELECT code, long_url, created_at, expires_at, deleted_at
            FROM links
            WHERE code = :code
            FOR UPDATE
            """)
                .param(Constants.CODE_COLUMN, code)
                .query(getLinkRowMapper()).optional();

        if (maybeLink.isEmpty()) {
            return DeleteResult.NOT_FOUND;
        }

        if (maybeLink.get().deletedAt() != null) {
            return DeleteResult.ALREADY_DELETED;
        }

        int update = jdbcClient.sql("""
                            UPDATE links
                            SET deleted_at = NOW()
                            WHERE code = :code
                        """)
                .param(Constants.CODE_COLUMN, code)
                .update();

        if (update == 0) {
            throw new IllegalStateException("Failed to mark link as deleted");
        }

        return DeleteResult.DELETED;
    }
}
