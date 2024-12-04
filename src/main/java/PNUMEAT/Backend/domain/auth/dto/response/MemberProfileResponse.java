package PNUMEAT.Backend.domain.auth.dto.response;

import PNUMEAT.Backend.domain.auth.entity.Member;

public record MemberProfileResponse(
        String memberName,
        String imageUrl,
        String memberExplain,
        String uuid
) {
    public static MemberProfileResponse of(Member member){
        return new MemberProfileResponse(
                member.getMemberName(),
                member.getImageUrl(),
                member.getDescription(),
                member.getUuid()
                );
    }
}
