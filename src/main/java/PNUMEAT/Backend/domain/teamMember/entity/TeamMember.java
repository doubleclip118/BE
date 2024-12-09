package PNUMEAT.Backend.domain.teamMember.entity;

import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Getter
@Entity
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

    protected TeamMember(){}

    public TeamMember(Team team, Member member) {
        this.member = member;
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember that = (TeamMember) o;
        return Objects.equals(teamInfoId, that.teamInfoId) && Objects.equals(member, that.member) && Objects.equals(team, that.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamInfoId, member, team);
    }
}
