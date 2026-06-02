package by.magofrays.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Integer value; // to understand hierarchy

    @ManyToOne
    Family family;

    @Builder.Default
    private List<String> accessList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "family_member_roles",
            joinColumns = @JoinColumn(name = "roles_id"),
            inverseJoinColumns = @JoinColumn(name = "family_member_id")
    )
    @Builder.Default
    private List<FamilyMember> familyMembers = new ArrayList<>();

    public void setFamily(Family family) {
        this.family = family;
        family.getRoles().add(this);
    }

    public void addFamilyMember(FamilyMember familyMember){
        familyMembers.add(familyMember);
    }

    public void removeFamilyMember(FamilyMember familyMember) {
        familyMembers.remove(familyMember);
    }
}
