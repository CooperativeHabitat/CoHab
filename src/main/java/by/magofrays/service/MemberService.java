package by.magofrays.service;

import by.magofrays.dto.request.UpdatePersonalInfoRequest;
import by.magofrays.dto.response.ReadFamilyMemberDto;
import by.magofrays.dto.response.ReadMemberDto;
import by.magofrays.dto.request.RegistrationRequest;
import by.magofrays.entity.Member;
import by.magofrays.entity.PersonalInfo;
import by.magofrays.entity.SuperRole;
import by.magofrays.exception.BusinessException;
import by.magofrays.mapper.MemberMapper;
import by.magofrays.repository.MemberRepository;
import by.magofrays.repository.PersonalInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ReadMemberDto createMember(RegistrationRequest registrationRequest) {
        log.info("Creating new member");
        Member member = memberMapper.toEntity(registrationRequest);
        member.setPassword(passwordEncoder.encode(registrationRequest.password()));
        member.setSuperRole(SuperRole.USER);
        PersonalInfo personalInfo = memberMapper.mapPersonalInfo(registrationRequest);
        personalInfo.setMember(member);
        member = memberRepository.save(member);
        personalInfoRepository.save(personalInfo);
        return memberMapper.toDto(member);
    }

    @Transactional
    public List<ReadFamilyMemberDto> getFamilyMembers(UUID memberId) {
        var member = memberRepository
                .findById(memberId).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Пользователь с id: " + memberId + " не существует."));
        return member.getFamilyMembers().stream().map(memberMapper::toDto).toList();
    }


    public boolean memberHasFamily(UUID memberID) {
        return !memberRepository
                .findById(memberID)
                .map(Member::getFamilyMembers).map(List::isEmpty)
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND,
                                "Пользователь с id: " + memberID + " не существует."));
    }

    public Optional<ReadMemberDto> findByUsername(String username) {
        return memberRepository.findByUsername(username).map(memberMapper::toDto);
    }

    @Transactional
    public ReadMemberDto updatePersonalInfo(UpdatePersonalInfoRequest personalInfoRequest) {
        var member = memberRepository.findByUsername(personalInfoRequest.username())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                                "Пользователь с id: " + personalInfoRequest.username() + " не существует.")
        );
        var personalInfo = member.getPersonalInfo();
        personalInfo.setBirthDate(personalInfoRequest.birthDate());
        personalInfo.setFirstname(personalInfoRequest.firstname());
        personalInfo.setLastname(personalInfoRequest.lastname());
        personalInfoRepository.save(personalInfo);
        return memberMapper.toDto(member);
    }
}
