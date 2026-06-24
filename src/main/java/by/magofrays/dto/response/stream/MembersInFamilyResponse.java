package by.magofrays.dto.response.stream;

import by.magofrays.dto.request.stream.MembersInFamilyRequest;

public record MembersInFamilyResponse(
        MembersInFamilyRequest member,
        Boolean isMemberInFamily
) {
}
