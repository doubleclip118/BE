package PNUMEAT.Backend.domain.teamMember.repository;

import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.enums.Topic;
import PNUMEAT.Backend.domain.team.repository.TeamRepository;
import PNUMEAT.Backend.domain.teamMember.entity.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class TeamMemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private Member teamManager;
    private Member memberHasTeam;
    private Member memberHasNotTeam;
    private Team team;
    private Team team2;
    private TeamMember teamMember1;
    private TeamMember teamMember2;

    @BeforeEach
    void setUp(){
        teamManager = new Member("test1@naver.com", "1", "ROLE_USER");
        memberHasTeam = new Member("test2@naver.com", "2", "ROLE_USER");
        memberHasNotTeam = new Member("test3@naver.com", "3", "ROLE_USER");

        memberRepository.save(teamManager);
        memberRepository.save(memberHasTeam);
        memberRepository.save(memberHasNotTeam);

        team = new Team("TEST", Topic.CODINGTEST, "TEST", 10, "1111", teamManager);
        team2 = new Team("TEST2", Topic.CODINGTEST, "TEST2", 2, "2222", teamManager);

        teamRepository.save(team);
        teamRepository.save(team2);

        teamMember1 = new TeamMember(team, memberHasTeam);
        teamMember2 = new TeamMember(team2, memberHasTeam);

        teamMemberRepository.save(teamMember1);
        teamMemberRepository.save(teamMember2);
    }

    @Test
    @DisplayName("멤버가 포함된 Team ID 리스트 반환하기 - 멤버가 포함된 팀이 있는 경우")
    void findTeamIdsByMemberId_팀이_있는_경우() {
        // given
        Long memberId = memberHasTeam.getId();
        Long teamId = team.getTeamId();
        Long teamId2 = team2.getTeamId();

        // when
        List<Long> teamIds = teamMemberRepository.findTeamIdsByMemberId(memberId);

        // then
        assertThat(teamIds).contains(teamId);
        assertThat(teamIds).contains(teamId2);
        assertThat(teamIds.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("멤버가 포함된 Team ID 리스트 반환하기 - 멤버가 포함된 팀이 없는 경우")
    void findTeamIdsByMemberId_팀이_없는_경우() {
        // given
        Long memberId = memberHasNotTeam.getId();

        // when
        List<Long> teamIds = teamMemberRepository.findTeamIdsByMemberId(memberId);

        // then
        assertThat(teamIds.size()).isEqualTo(0);
    }
}