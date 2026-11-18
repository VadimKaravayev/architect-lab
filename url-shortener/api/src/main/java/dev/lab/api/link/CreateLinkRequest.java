package dev.lab.api.link;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateLinkRequest(
        @NotBlank
        @Size(max = 2048)
        @Pattern(regexp = "(?i)https?://.+")
        String url,
        @Future
        Instant expiresAt) {
}
