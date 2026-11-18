package dev.lab.api.link;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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

    public void insert(String code, String longUrl, Instant expiresAt) {

        OffsetDateTime offsetDateTime = expiresAt == null
                ? null
                : expiresAt.atOffset(ZoneOffset.UTC);
        jdbcClient
                .sql("insert into links (code, long_url, expires_at) values (:code, :longUrl, :expiresAt)")
                .param("code", code)
                .param("longUrl", longUrl)
                .param("expiresAt", offsetDateTime)
                .update();
    }
}
