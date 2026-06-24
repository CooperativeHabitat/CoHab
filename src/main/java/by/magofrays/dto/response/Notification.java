package by.magofrays.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;


public record Notification (
    String from,
    UUID recipient,
    String message,
    Instant createdAt
) {}
