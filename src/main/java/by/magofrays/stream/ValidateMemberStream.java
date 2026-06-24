package by.magofrays.stream;

import by.magofrays.dto.request.stream.MembersInFamilyRequest;
import by.magofrays.dto.response.stream.MembersInFamilyResponse;
import by.magofrays.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class ValidateMemberStream {
    private final FamilyService familyService;

    @Bean
    Function<MembersInFamilyRequest, MembersInFamilyResponse> isMembersInFamily(){
        return (members) -> {
            var isMembersInFamily = familyService.isMembersInFamily(members.memberIds(), members.familyId());
            return new MembersInFamilyResponse(members, isMembersInFamily);
        };
    }

}
