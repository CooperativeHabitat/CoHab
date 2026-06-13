package by.magofrays.dto.response;

import java.util.UUID;


public record MemberDto (
    UUID id,
    String username,
    PersonalInfoDto personalInfo
    ) {}
