package by.magofrays.repository;

import by.magofrays.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {
    Optional<FamilyMember> findByMember_usernameAndFamily_Id(String username, UUID familyId);

    @Query("""
        SELECT COUNT(fm) > 0
        FROM FamilyMember fm
        WHERE fm.family.id = :familyId AND fm.member.id = :memberId
    """)
    Boolean isMemberInFamily(@Param("memberId") UUID memberId, @Param("familyId") UUID familyId);

    @Query("""
        SELECT COUNT(DISTINCT fm.member.id) = (SELECT COUNT(m) FROM Member m WHERE m.id IN :memberIds)
        FROM FamilyMember fm
        WHERE fm.family.id = :familyId AND fm.member.id IN :memberIds
    """)
    Boolean isMembersInFamily(@Param("memberIds") List<UUID> memberIds, @Param("familyId") UUID familyId );

    Optional<FamilyMember> findByMember_IdAndFamily_Id(UUID memberId, UUID familyId);


    List<FamilyMember> findByFamily_Id(UUID familyId);
}
