package by.magofrays.mapper;

import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.ReadMemberDto;
import by.magofrays.dto.request.RegistrationRequest;
import by.magofrays.dto.response.SmallFamilyMemberDto;
import by.magofrays.entity.FamilyMember;
import by.magofrays.entity.Member;
import by.magofrays.entity.PersonalInfo;
import by.magofrays.security.MemberPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {PersonalInfoMapper.class, FamilyMapper.class, RoleMapper.class})
public abstract class MemberMapper {

    @Mapping(target = "member", source = "familyMember.member")
    public abstract ReadFamilyMemberDto toDto(FamilyMember familyMember);

    @Mapping(target = "username", source = "member.username")
    @Mapping(target = "personalInfo", source = "member.personalInfo")
    public abstract SmallFamilyMemberDto toSmallDto(FamilyMember familyMember);

    @Mapping(target = "personalInfo", source = "personalInfo")
    public abstract ReadMemberDto toDto(Member member);

    @Mapping(target = "personalInfo", ignore = true)
    public abstract ReadMemberDto memberDto(MemberPrincipal principal);

    @Mapping(target = "password", ignore = true)
    public abstract Member toEntity(RegistrationRequest registrationRequest);

    public PersonalInfo mapPersonalInfo(RegistrationRequest registrationRequest) {
        return PersonalInfo.builder()
                .firstname(registrationRequest.firstname())
                .lastname(registrationRequest.lastname())
                .birthDate(registrationRequest.birthDate())
                .build();
    }
}