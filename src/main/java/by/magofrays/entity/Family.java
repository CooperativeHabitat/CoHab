package by.magofrays.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

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

    @OneToMany(mappedBy = "family")
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
