package PNUMEAT.Backend.domain.team.repository;

import PNUMEAT.Backend.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
