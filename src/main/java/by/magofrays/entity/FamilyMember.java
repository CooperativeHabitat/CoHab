package by.magofrays.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Family family;

    @Builder.Default
    @ManyToMany(mappedBy = "familyMembers")
    private List<Role> roles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "createdBy")
    private List<Task> createdTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "issuedTo")
    private List<Task> issuedTasks = new ArrayList<>();

    private LocalDateTime addedAt;

    public void addRole(Role role) {
        roles.add(role);
    }
}
