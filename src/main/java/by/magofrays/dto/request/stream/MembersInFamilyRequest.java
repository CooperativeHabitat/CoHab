package by.magofrays.dto.request.stream;

import java.util.List;
import java.util.UUID;

public record MembersInFamilyRequest(
        List<UUID> memberIds,
        UUID familyId
) {
}
