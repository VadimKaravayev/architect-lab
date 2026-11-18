package dev.lab.api.link;

import java.time.Instant;

public record CreateLinkCommand(String url, Instant expiresAt) {
}
