package by.magofrays.security;


import by.magofrays.entity.SuperRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class MemberPrincipal implements UserDetails {
    @Getter
    private UUID id;
    private String username;
    @JsonIgnore
    private String password;
    private SuperRole superRole;

    @Override
    public List<SuperRole> getAuthorities() {
        return List.of(superRole);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
