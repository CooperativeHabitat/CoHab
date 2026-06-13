package by.magofrays.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReadFamilyMemberDto {
    UUID id;
    MemberDto member;
    ReadFamilyDto family;
    List<RoleResponse> roles;
    private LocalDateTime addedAt;
}
