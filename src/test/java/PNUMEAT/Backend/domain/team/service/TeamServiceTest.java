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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private ImageService imageService;

    @Mock
    private TeamRepository teamRepository; // teamRepository를 모킹

    @Mock
    private TeamMemberRepository teamMemberRepository; // TeamMemberRepository도 모킹

    @InjectMocks
    private TeamService teamService;

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
}