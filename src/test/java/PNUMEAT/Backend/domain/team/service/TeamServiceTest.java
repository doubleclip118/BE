package PNUMEAT.Backend.domain.team.service;

import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.team.dto.request.TeamRequest;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.enums.Topic;
import PNUMEAT.Backend.domain.team.repository.TeamRepository;
import PNUMEAT.Backend.domain.teamMember.entity.TeamMember;
import PNUMEAT.Backend.domain.teamMember.repository.TeamMemberRepository;
import PNUMEAT.Backend.global.images.ImageConstant;
import PNUMEAT.Backend.global.images.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private ImageService imageService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamService teamService;

    private Member member;
    private Team team2;
    private Team team3;
    private Team team4;

    @BeforeEach
    void setUp() {
        member = new Member("TEST", "TEST", "ROLE_USER");

        team2 = createTeam("팀2", "팀2 설명", 10, member);
        team3 = createTeam("팀3", "팀3 설명", 10, member);
        team4 = createTeam("팀4", "팀4 설명", 10, member);
    }

    @Test
    @DisplayName("팀 등록 시 teamIcon을 등록하지 않았다면 기본 이미지가 등록된다.")
    void 팀_등록_시_기본_이미지_등록_테스트() {
        //given
        TeamRequest teamRequest = new TeamRequest(
                "한글Team1",
                "이 팀은 예시 팀입니다!",
                "스터디",
                10,
                "1234"
        );
        Member member = new Member("TEST", "TEST", "ROLE_USER");
        MultipartFile teamIcon = null;

        Team team = Team.builder()
                .teamName(teamRequest.teamName())
                .teamExplain(teamRequest.teamExplain())
                .teamTopic(Topic.fromName(teamRequest.topic()))
                .maxParticipant(teamRequest.memberLimit())
                .teamPassword(teamRequest.password())
                .teamManager(member)
                .build();

        given(teamRepository.save(any(Team.class))).willReturn(team);

        TeamMember teamMember = new TeamMember(team, member);
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

        //when
        Team createdTeam = teamService.createTeam(teamRequest, teamIcon, member);

        //then
        assertThat(createdTeam.getTeamIconUrl()).isEqualTo(ImageConstant.DEFAULT_TEAM_IMAGE_URL);
        assertThat(createdTeam.getTeamName()).isEqualTo(teamRequest.teamName());
        assertThat(createdTeam.getTeamManager().getMemberName()).isEqualTo(member.getMemberName());
    }

    @Test
    @DisplayName("회원 프로필 등록 시 image를 등록하면 imageUrl이 변경된다.")
    void 프로필_등록_시_이미지_등록_테스트() {
        //given
        TeamRequest teamRequest = new TeamRequest(
                "한글Team1",
                "이 팀은 예시 팀입니다!",
                "스터디",
                10,
                "1234"
        );

        MultipartFile teamIcon = new MockMultipartFile("TEST",
                "TEST.png",
                MediaType.IMAGE_PNG_VALUE,
                "TEST".getBytes());

        Member member = new Member("TEST", "TEST", "ROLE_USER");

        Team team = Team.builder()
                .teamName(teamRequest.teamName())
                .teamExplain(teamRequest.teamExplain())
                .teamTopic(Topic.fromName(teamRequest.topic()))
                .maxParticipant(teamRequest.memberLimit())
                .teamPassword(teamRequest.password())
                .teamManager(member)
                .build();
        team.updateTeamIconUrl("s3://");

        given(imageService.teamImageUpload(teamIcon)).willReturn("s3://");

        given(teamRepository.save(any(Team.class))).willReturn(team);

        TeamMember teamMember = new TeamMember(team, member);
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

        //when
        Team createdTeam = teamService.createTeam(teamRequest, teamIcon, member);

        //then
        assertThat(createdTeam.getTeamIconUrl()).isEqualTo("s3://");
        assertThat(createdTeam.getTeamName()).isEqualTo(teamRequest.teamName());
        assertThat(createdTeam.getTeamManager().getMemberName()).isEqualTo(member.getMemberName());
    }

    @Test
    @DisplayName("페이지네이션을 적용하여 모든 팀 조회하기")
    void getAllTeams_페이지네이션_적용_시() {
        // given
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Team> teams = Arrays.asList(team4, team3, team2);
        Page<Team> mockPage = new PageImpl<>(teams, pageable, 4);

        given(teamRepository.findAll(pageable)).willReturn(mockPage);

        // when
        Page<Team> result = teamService.getAllTeams(pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent().get(0)).isEqualTo(team4);
    }

    @Test
    @DisplayName("멤버가 속한 마지막 팀 조회하기 - 속한 팀이 있는 경우")
    void getMyTeam_팀이_있을_때() {
        // given
        List<Long> teamIds = Arrays.asList(1L, 2L);
        given(teamMemberRepository.findTeamIdsByMemberId(member.getId())).willReturn(teamIds);
        given(teamRepository.findTeamsWithMembersByTeamIds(Arrays.asList(teamIds.getLast()))).willReturn(Arrays.asList(team2));

        // when
        List<Team> result = teamService.getMyTeam(member);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(team2);
    }

    @Test
    @DisplayName("멤버가 속한 마지막 팀 조회하기 - 속한 팀이 없는 경우")
    void getMyTeam_속한_팀이_없을_때() {
        // given
        given(teamMemberRepository.findTeamIdsByMemberId(member.getId())).willReturn(Collections.emptyList());

        // when
        List<Team> result = teamService.getMyTeam(member);

        // then
        assertThat(result).isEmpty();
    }

    private Team createTeam(String teamName, String teamExplain, int maxParticipant, Member teamManager) {
        return Team.builder()
                .teamName(teamName)
                .teamExplain(teamExplain)
                .teamTopic(Topic.CODINGTEST)
                .maxParticipant(maxParticipant)
                .teamPassword("1111")
                .teamManager(teamManager)
                .build();
    }
}