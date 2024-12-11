package PNUMEAT.Backend.domain.team.service;

import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.team.dto.request.TeamRequest;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.enums.Topic;
import PNUMEAT.Backend.domain.team.repository.TeamRepository;
import PNUMEAT.Backend.domain.teamMember.entity.TeamMember;
import PNUMEAT.Backend.domain.teamMember.repository.TeamMemberRepository;
import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.images.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static PNUMEAT.Backend.global.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ImageService imageService;

    @Transactional
    public Team createTeam(TeamRequest teamRequest,
                           MultipartFile teamIcon, Member member){

        Team team = Team.builder()
                .teamName(teamRequest.teamName())
                .teamExplain(teamRequest.teamExplain())
                .teamTopic(Topic.fromName(teamRequest.topic()))
                .maxParticipant(teamRequest.memberLimit())
                .teamPassword(teamRequest.password())
                .teamManager(member)
                .build();

        if (teamIcon != null){
            String teamIconUrl = imageService.teamImageUpload(teamIcon);
            team.updateTeamIconUrl(teamIconUrl);
        }

        TeamMember teamMember = new TeamMember(team, member);

        teamMemberRepository.save(teamMember);

        return teamRepository.save(team);
    }

    public Page<Team> getAllTeams(Pageable pageable){
        return teamRepository.findAll(pageable);
    }

    public List<Team> getMyTeam(Member member){
        List<Long> teamIds = teamMemberRepository.findTeamIdsByMemberId(member.getId());

        if(teamIds.isEmpty()){
            return Collections.emptyList();
        }
        return teamRepository.findTeamsWithMembersByTeamIds(Arrays.asList(teamIds.getLast()));
    }

    public void joinTeam(Member member, String password, Long teamId){
        Team team = findTeamById(teamId);

        validatePassword(password, team);
        validateTeamMembership(team, member);

        teamMemberRepository.save(new TeamMember(team, member));
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ComonException(TEAM_NOT_FOUND_ERROR));
    }

    private void validatePassword(String password, Team team) {
        if (!password.equals(team.getTeamPassword())) {
            throw new ComonException(TEAM_PASSWORD_INVALID);
        }
    }

    private void validateTeamMembership(Team team, Member member) {
        if (teamMemberRepository.existsByTeamAndMember(team, member)) {
            throw new ComonException(TEAM_ALREADY_JOIN);
        }
    }
}
