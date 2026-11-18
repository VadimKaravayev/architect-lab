package dev.lab.api.link;

import java.time.Instant;

public record Link(String code, String longUrl, Instant expiresAt) {
}
