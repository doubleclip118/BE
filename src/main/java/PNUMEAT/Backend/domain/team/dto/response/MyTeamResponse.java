package PNUMEAT.Backend.domain.team.dto.response;

import PNUMEAT.Backend.domain.auth.dto.response.MemberProfileResponse;
import PNUMEAT.Backend.domain.team.entity.Team;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record MyTeamResponse(Long teamId,
                              String teamName,
                              String teamExplain,
                              String imageUrl,
                              String topic,
                              int memberLimit,
                              int memberCount,
                              int streakDays,
                              int successMemberCount,
                              LocalDate createdAt,
                              List<MemberProfileResponse> members) {

    public static MyTeamResponse of(Team team){
        return new MyTeamResponse(
                team.getTeamId(),
                team.getTeamName(),
                team.getTeamExplain(),
                team.getTeamIconUrl(),
                team.getTeamTopic().getName(),
                team.getMaxParticipant(),
                team.getTeamMembers().size(),
                team.getStreakDays(),
                0, // 게시물 엔티티 없는 관계로 0으로 설정
                team.getCreatedDate().toLocalDate(),
                team.getTeamMembers().stream()
                        .map(tm -> MemberProfileResponse.of(tm.getMember()))
                        .collect(Collectors.toList()) // 현재 게시물 작성 멤버 말고 전체 멤버 제공 중
        );
    }
}
