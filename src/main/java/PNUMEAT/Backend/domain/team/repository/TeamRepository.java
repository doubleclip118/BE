package PNUMEAT.Backend.domain.team.repository;

import PNUMEAT.Backend.domain.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Page<Team> findAll(Pageable pageable);

    @Query("SELECT DISTINCT t FROM Team t " +
            "JOIN FETCH t.teamMembers tm " +
            "JOIN FETCH tm.member " +
            "WHERE t.teamId IN :teamIds")
    List<Team> findTeamsWithMembersByTeamIds(@Param("teamIds") List<Long> teamIds);

}
