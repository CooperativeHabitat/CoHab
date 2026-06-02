package by.magofrays.dto.response;

import java.util.UUID;

public record ReadFamilyDto(
        UUID id,
        String familyName,
        UUID createdBy,
        Integer memberCount
) {
}
