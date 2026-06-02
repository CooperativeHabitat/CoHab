package by.magofrays.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String familyName;

    @OneToMany(mappedBy = "family", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<FamilyMember> members = new ArrayList<>();

    @ManyToOne
    private Member createdBy;

    @OneToMany(mappedBy = "family")
    private List<Role> roles;

    public void addMember(FamilyMember familyMember) {
        familyMember.setAddedAt(LocalDateTime.now());
        members.add(familyMember);
    }
}
