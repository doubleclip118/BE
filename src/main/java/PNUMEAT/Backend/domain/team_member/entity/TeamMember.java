package PNUMEAT.Backend.domain.team_member.entity;

import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.team.entity.Team;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamInfoId;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne
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
