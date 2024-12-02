package PNUMEAT.Backend.domain.team_member.repository;

import PNUMEAT.Backend.domain.team_member.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
