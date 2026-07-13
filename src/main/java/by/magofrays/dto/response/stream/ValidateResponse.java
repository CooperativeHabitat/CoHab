package by.magofrays.dto.response.stream;

import java.util.UUID;

public record MembersInFamilyResponse(
        UUID eventId,
        Boolean isMemberInFamily
) {
}
