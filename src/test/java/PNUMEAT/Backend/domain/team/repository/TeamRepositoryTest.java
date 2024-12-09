package PNUMEAT.Backend.domain.team.repository;

import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.enums.Topic;
import PNUMEAT.Backend.domain.teamMember.entity.TeamMember;
import PNUMEAT.Backend.domain.teamMember.repository.TeamMemberRepository;
import PNUMEAT.Backend.global.security.configuration.JpaAuditingConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Import(JpaAuditingConfiguration.class)
class TeamRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamMemberRepository teamMemberRepository;

    @Autowired
    EntityManager entityManager;

    private int lastRegisteredIndex = 20;
    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp(){
        Member member = new Member("TEST", "TEST", "ROLE_USER");
        memberRepository.save(member);

        for(int i=1; i<=lastRegisteredIndex; i++){
            Team team = Team.builder()
                    .teamName("팀"+i)
                    .teamExplain("팀"+i+" 입니다")
                    .teamTopic(Topic.CODINGTEST)
                    .maxParticipant(i)
                    .teamPassword("1111")
                    .teamManager(member)
                    .build();

            teamRepository.save(team);
        }
    }

    @Test
    @DisplayName("전체 조회 페이지네이션 테스트")
    void findAll_페이지네이션_테스트() {
        // given
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdDate"));

        // when
        Page<Team> teamWithPage = teamRepository.findAll(pageable);
        List<Team> teams = teamWithPage.stream().toList();

        // then
        assertAll(
                () -> assertThat(teamWithPage.getTotalPages()).isEqualTo(4),
                () -> assertThat(teamWithPage.getNumberOfElements()).isEqualTo(6),
                () -> assertThat(teams.getFirst().getTeamName()).isEqualTo("팀"+lastRegisteredIndex),
                () -> assertThat(teams.getFirst().getMaxParticipant()).isEqualTo(lastRegisteredIndex)
        );
    }

    @Test
    @DisplayName("Team ID 리스트로 팀 정보와 해당 팀 멤버 정보 가져오기")
    void findTeamsWithMembersByTeamIds() {
        // given
        Member member1 = createAndSaveMember("TEST1", "TEST1");
        Member member2 = createAndSaveMember("TEST2", "TEST2");
        Member member3 = createAndSaveMember("TEST3", "TEST3");

        Team team1 = createAndSaveTeam("팀1", member1, 1);
        Team team2 = createAndSaveTeam("팀2", member1, 2);

        List<Member> team1Members = Arrays.asList(member1, member2, member3);
        List<Member> team2Members = Arrays.asList(member1, member2);

        addTeamMembers(team1, team1Members);
        addTeamMembers(team2, team2Members);

        teamMemberRepository.flush();

        entityManager.refresh(team1);
        entityManager.refresh(team2);

        List<Long> teamIds = Arrays.asList(team1.getTeamId(), team2.getTeamId());

        // when
        List<Team> teams = teamRepository.findTeamsWithMembersByTeamIds(teamIds);

        // then
        assertThat(teams.size()).isEqualTo(2);

        Team resultTeam1 = findTeamById(teams, team1.getTeamId());
        Team resultTeam2 = findTeamById(teams, team2.getTeamId());

        assertThat(resultTeam1).isNotNull();
        assertThat(resultTeam1.getTeamName()).isEqualTo(team1.getTeamName());
        assertThat(resultTeam1.getTeamMembers().size()).isEqualTo(3);
        assertThat(resultTeam1.getTeamMembers()).extracting(tm -> tm.getMember().getEmail())
                .containsExactlyInAnyOrder("TEST1", "TEST2", "TEST3");

        assertThat(resultTeam2).isNotNull();
        assertThat(resultTeam2.getTeamName()).isEqualTo(team2.getTeamName());
        assertThat(resultTeam2.getTeamMembers().size()).isEqualTo(2);
        assertThat(resultTeam2.getTeamMembers()).extracting(tm -> tm.getMember().getEmail())
                .containsExactlyInAnyOrder("TEST1", "TEST2");
    }


    private Member createAndSaveMember(String email, String uniqueId) {
        Member member = new Member(email, uniqueId, "ROLE_USER");
        return memberRepository.save(member);
    }

    private Team createAndSaveTeam(String teamName, Member manager, int maxParticipant) {
        Team team = Team.builder()
                .teamName(teamName)
                .teamExplain(teamName + " 입니다")
                .teamTopic(Topic.CODINGTEST)
                .maxParticipant(maxParticipant)
                .teamPassword("1111")
                .teamManager(manager)
                .build();
        return teamRepository.save(team);
    }

    private void addTeamMembers(Team team, List<Member> members) {
        for (Member member : members) {
            teamMemberRepository.save(new TeamMember(team, member));
        }
    }

    private Team findTeamById(List<Team> teams, Long teamId) {
        return teams.stream()
                .filter(team -> team.getTeamId().equals(teamId))
                .findFirst()
                .orElse(null);
    }

}