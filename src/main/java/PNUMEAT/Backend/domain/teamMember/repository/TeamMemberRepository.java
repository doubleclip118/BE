package PNUMEAT.Backend.domain.teamMember.repository;

import PNUMEAT.Backend.domain.teamMember.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("SELECT tm.team.teamId " +
            "FROM TeamMember tm " +
            "WHERE tm.member.id = :memberId")
    List<Long> findTeamIdsByMemberId(@Param("memberId") Long memberId);
}