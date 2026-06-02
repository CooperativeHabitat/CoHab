package by.magofrays.mapper;

import by.magofrays.dto.response.ReadFamilyDto;
import by.magofrays.entity.Family;
import by.magofrays.repository.MemberRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public abstract class FamilyMapper {
    @Autowired
    private MemberRepository memberRepository;

    @Mapping(target = "createdBy", expression = "java(family.getCreatedBy().getId())")
    @Mapping(target = "memberCount", expression = "java(family.getMembers().size())")
    public abstract ReadFamilyDto toDto(Family family);

}
