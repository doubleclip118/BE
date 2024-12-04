package PNUMEAT.Backend.domain.auth.dto.request;

import PNUMEAT.Backend.global.error.Member.MemberProfileUpdateValidException;

public record MemberProfileUpdateRequest(
        String memberName,
        String memberExplain
) {

    public MemberProfileUpdateRequest {
        if (memberName == null && memberExplain == null) {
            throw new MemberProfileUpdateValidException();
        }
    }
}
